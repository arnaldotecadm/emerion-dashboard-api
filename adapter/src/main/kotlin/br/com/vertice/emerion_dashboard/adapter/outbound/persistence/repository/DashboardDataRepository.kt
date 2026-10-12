package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardTopTrendsProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardTotaisProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardMonthlyTrendProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardOrdersByRegionProjection
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class DashboardDataRepository(
    @PersistenceContext
    private val entityManager: EntityManager
) {

    fun getDashboardOrdersByRegion(): List<DashboardOrdersByRegionProjection> {
        val query = entityManager.createNativeQuery(
            """
            SELECT
                CASE
                    WHEN co.uf_pedido IN ('AC', 'AP', 'AM', 'PA', 'RO', 'RR', 'TO') THEN 'Norte'
                    WHEN co.uf_pedido IN ('AL', 'BA', 'CE', 'MA', 'PB', 'PE', 'PI', 'RN', 'SE') THEN 'Nordeste'
                    WHEN co.uf_pedido IN ('DF', 'GO', 'MT', 'MS') THEN 'Centro-Oeste'
                    WHEN co.uf_pedido IN ('ES', 'MG', 'RJ', 'SP') THEN 'Sudeste'
                    WHEN co.uf_pedido IN ('PR', 'RS', 'SC') THEN 'Sul'
                    ELSE 'Não identificado'
                END AS regiao,
                COALESCE(SUM(CASE WHEN co.status_pedido = 'Faturado'
                    THEN co.total_pedido_com_impostos ELSE 0 END), 0) AS faturado,
                COALESCE(SUM(CASE WHEN co.status_pedido = 'Cancelado'
                    THEN co.total_pedido_com_impostos ELSE 0 END), 0) AS cancelado,
                COALESCE(SUM(CASE WHEN co.status_pedido NOT IN ('Faturado', 'Cancelado')
                                     OR co.status_pedido IS NULL
                    THEN co.total_pedido_com_impostos ELSE 0 END), 0) AS outros
            FROM emerion_dashboard.customer_order co
            GROUP BY regiao
            ORDER BY SUM(co.total_pedido_com_impostos) DESC
            """.trimIndent()
        )
        return query.resultList.map { row ->
            val resultArray = row as Array<*>
            DashboardOrdersByRegionProjection(
                region = resultArray[0].toString(),
                faturado = resultArray[1] as BigDecimal,
                cancelado = resultArray[2] as BigDecimal,
                outros = resultArray[3] as BigDecimal,
            )
        }
    }

    fun getDashboardMonthlyTrends(years: Int?): List<DashboardMonthlyTrendProjection> {
        val query = entityManager.createNativeQuery(
            """
            with bounds as (
                select case
                    when cast(:years as integer) is null
                        then (date_trunc('month', current_date) - interval '35 months')::date
                    else make_date(extract(year from current_date)::int - cast(:years as integer) + 1, 1, 1)
                end as start_date
            ),
            months as (
                select generate_series(
                    (select start_date from bounds),
                    date_trunc('month', current_date),
                    interval '1 month'
                )::date as month_start
            ),
            customer_orders as (
                select
                    date_trunc('month', co.data_pedido)::date as month_start,
                    count(*) as item_count,
                    coalesce(sum(co.total_pedido_com_impostos), 0) as amount_sold
                from emerion_dashboard.customer_order co
                where co.data_pedido >= (select start_date from bounds)
                  and co.data_pedido <= current_date
                group by date_trunc('month', co.data_pedido)::date
            ),
            customer_quotations as (
                select date_trunc('month', cq.data_pedido)::date as month_start, count(*) as item_count
                from emerion_dashboard.customer_quotation cq
                where cq.data_pedido >= (select start_date from bounds)
                  and cq.data_pedido <= current_date
                group by date_trunc('month', cq.data_pedido)::date
            )
            select
                to_char(months.month_start, 'YYYY-MM') as year_month,
                coalesce(customer_orders.item_count, 0) as customer_orders,
                coalesce(customer_orders.amount_sold, 0) as amount_sold,
                coalesce(customer_quotations.item_count, 0) as customer_quotations
            from months
            left join customer_orders on customer_orders.month_start = months.month_start
            left join customer_quotations on customer_quotations.month_start = months.month_start
            order by months.month_start
            """.trimIndent()
        )
        query.setParameter("years", years)
        return query.resultList.map { row ->
            val resultArray = row as Array<*>
            DashboardMonthlyTrendProjection(
                yearMonth = resultArray[0].toString(),
                customerOrders = (resultArray[1] as Number).toInt(),
                customerOrdersAmountSold = resultArray[2] as BigDecimal,
                customerQuotations = (resultArray[3] as Number).toInt(),
            )
        }
    }

    fun getDashboardCustomerTopTrends(orderBy: String): List<DashboardTopTrendsProjection> {
        val query = entityManager.createNativeQuery(
            """
            with top_costumers as (
                select 
                    co.codigo_cliente as CodigoCliente, 
                    count(1) as quantidadePedido, 
                    sum(co.total_pedido_com_impostos) as totalFaturado 
                from emerion_dashboard.customer_order co
                group by co.codigo_cliente
            )
            select 
                coalesce(c.id, tc.codigocliente),
                coalesce(c.razao_social, ''),
                tc.quantidadepedido, 
                tc.totalfaturado  
            from emerion_dashboard.customer c
            right join top_costumers tc on tc.CodigoCliente = c.external_id::int
            order by tc.${orderBy} desc
            limit 10
        """.trimIndent()
        )
        return query.resultList.map { row ->
            val resultArray = row as Array<*>
            DashboardTopTrendsProjection(
                identifier = resultArray[0].toString(),
                name = resultArray[1].toString(),
                amountSales = (resultArray[2] as Number).toInt(),
                totalSales = (resultArray[3] as BigDecimal)
            )
        }
    }

    fun getDashboardSellerTopTrends(orderBy: String, years: Int?): List<DashboardTopTrendsProjection> {
        val query = entityManager.createNativeQuery(
            """
            with top_vendedores as (
            	select 
            		co.vendedor_external_id as CodigoVendedor, 
            		count(1) as quantidadePedido, 
            		sum(co.total_pedido_com_impostos) as totalFaturado 
            	from emerion_dashboard.customer_order co
            	where cast(:years as integer) is null
            	   or extract(year from co.data_pedido) >= extract(year from current_date) - cast(:years as integer) + 1
            	group by co.vendedor_external_id 
            )
            select 
            	coalesce(v.id, tc.CodigoVendedor), 
            	coalesce(v.nome, ''), 
            	tc.quantidadepedido , 
            	tc.totalfaturado  
            from emerion_dashboard.vendedor v
            right join top_vendedores tc on tc.CodigoVendedor = v.external_id::int
            order by tc.${orderBy} desc
            limit 10
        """.trimIndent()
        )
        query.setParameter("years", years)
        return query.resultList.map { row ->
            val resultArray = row as Array<*>
            DashboardTopTrendsProjection(
                identifier = resultArray[0].toString(),
                name = resultArray[1].toString(),
                amountSales = (resultArray[2] as Number).toInt(),
                totalSales = (resultArray[3] as BigDecimal)
            )
        }
    }

    fun getDashboardTotais(years: Int?): DashboardTotaisProjection {
        val query = entityManager.createNativeQuery(
            """
            with pedidos as (
                select
                    codigo_cliente,
                    vendedor_external_id,
                    total_pedido_com_impostos
                from emerion_dashboard.customer_order
                where cast(:years as integer) is null
                   or extract(year from data_pedido) >= extract(year from current_date) - cast(:years as integer) + 1
            ),
            totais_clientes as (
                select count(distinct(id)) as quantidadeClientes
                from emerion_dashboard.customer c
                right join pedidos co on c.id = co.codigo_cliente
            ),
            totais_vendedores as (
                select count(distinct(id)) as quantidadeVendedores
                from emerion_dashboard.vendedor v
                right join pedidos co on v.external_id::int = co.vendedor_external_id
            ),
            pedido_totais as (
                select
                    count(1) as quantidadePedidos,
                    coalesce(sum(total_pedido_com_impostos), 0) as totalFaturado
                from pedidos
            )
            select
                quantidadePedidos,
                totalFaturado,
                quantidadeClientes,
                quantidadeVendedores
            from pedido_totais
            cross join totais_clientes
            cross join totais_vendedores
        """.trimIndent()
        )
        query.setParameter("years", years)
        val resultArray = query.singleResult as Array<*>
        return DashboardTotaisProjection(
            quantidadePedidos = (resultArray[0] as Number).toInt(),
            totalFaturado = (resultArray[1] as BigDecimal),
            quantidadeClientes = (resultArray[2] as Number).toInt(),
            quantidadeVendedores = (resultArray[3] as Number).toInt()
        )
    }
}