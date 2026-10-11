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

    fun getDashboardMonthlyTrends(): List<DashboardMonthlyTrendProjection> {
        val query = entityManager.createNativeQuery(
            """
            with months as (
                select generate_series(
                    date_trunc('month', current_date) - interval '35 months',
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
                where co.data_pedido >= date_trunc('month', current_date) - interval '35 months'
                  and co.data_pedido <= current_date
                group by date_trunc('month', co.data_pedido)::date
            ),
            customer_quotations as (
                select date_trunc('month', cq.data_pedido)::date as month_start, count(*) as item_count
                from emerion_dashboard.customer_quotation cq
                where cq.data_pedido >= date_trunc('month', current_date) - interval '35 months'
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

    fun getDashboardSellerTopTrends(orderBy: String): List<DashboardTopTrendsProjection> {
        val query = entityManager.createNativeQuery(
            """
            with top_vendedores as (
            	select 
            		co.vendedor_external_id as CodigoVendedor, 
            		count(1) as quantidadePedido, 
            		sum(co.total_pedido_com_impostos) as totalFaturado 
            	from emerion_dashboard.customer_order co
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

    fun getDashboardTotais(): DashboardTotaisProjection {
        val query = entityManager.createNativeQuery(
            """
            select 
                count(1) quantidadePedidos, 
                coalesce(sum(co.total_pedido_com_impostos), 0) totalFaturado,
                (select count(1) from emerion_dashboard.customer c) quantidadeClientes,
                (select count(1) from emerion_dashboard.vendedor) quantidadeVendedores
            from emerion_dashboard.customer_order co 
        """.trimIndent()
        )
        val resultArray = query.singleResult as Array<*>
        return DashboardTotaisProjection(
            quantidadePedidos = (resultArray[0] as Number).toInt(),
            totalFaturado = (resultArray[1] as BigDecimal),
            quantidadeClientes = (resultArray[2] as Number).toInt(),
            quantidadeVendedores = (resultArray[3] as Number).toInt()
        )
    }
}