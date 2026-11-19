package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardCockpitMetricsProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardOverdueInvoiceProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardRecentOrderProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardUnpaidInvoicesProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardTopTrendsProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardTotaisProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardMonthlyTrendProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardNewCustomersByMonthProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardOrdersByRegionProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardCustomerRegionProjection
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate

@Repository
class DashboardDataRepository(
    @PersistenceContext
    private val entityManager: EntityManager
) {

    fun getDashboardOrdersByRegion(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): List<DashboardOrdersByRegionProjection> {
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
            WHERE (
                    (
                        (cast(:startDate as date) is not null or cast(:endDate as date) is not null)
                        and co.data_pedido >= coalesce(cast(:startDate as date), date '1900-01-01')
                        and co.data_pedido <= coalesce(cast(:endDate as date), current_date)
                    )
                    or (
                        cast(:startDate as date) is null
                        and cast(:endDate as date) is null
                        and (
                            cast(:years as integer) is null
                            or extract(year from co.data_pedido) >= extract(year from current_date) - cast(:years as integer) + 1
                        )
                    )
                )
              AND co.cnpj_empresa = :cnpjEmpresa
            GROUP BY regiao
            ORDER BY SUM(co.total_pedido_com_impostos) DESC
            """.trimIndent()
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
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

    fun getDashboardMonthlyTrends(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): List<DashboardMonthlyTrendProjection> {
        val query = entityManager.createNativeQuery(
            """
            with bounds as (
                select case
                    when cast(:startDate as date) is not null
                        then cast(:startDate as date)
                    when cast(:endDate as date) is not null
                        then (date_trunc('month', cast(:endDate as date)) - interval '35 months')::date
                    when cast(:years as integer) is null
                        then (date_trunc('month', current_date) - interval '35 months')::date
                    else make_date(extract(year from current_date)::int - cast(:years as integer) + 1, 1, 1)
                end as start_date
            ),
            months as (
                select generate_series(
                    (select start_date from bounds),
                    date_trunc('month', coalesce(cast(:endDate as date), current_date)),
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
                  and co.data_pedido <= coalesce(cast(:endDate as date), current_date)
                  and co.cnpj_empresa = :cnpjEmpresa
                group by date_trunc('month', co.data_pedido)::date
            ),
            customer_quotations as (
                select date_trunc('month', cq.data_pedido)::date as month_start, count(*) as item_count
                from emerion_dashboard.customer_quotation cq
                where cq.data_pedido >= (select start_date from bounds)
                  and cq.data_pedido <= coalesce(cast(:endDate as date), current_date)
                  and cq.cnpj_empresa = :cnpjEmpresa
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
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
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

    fun getDashboardCustomerTopTrends(
        orderBy: String,
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): List<DashboardTopTrendsProjection> {
        val query = entityManager.createNativeQuery(
            """
            with top_costumers as (
                select 
                    co.codigo_cliente as CodigoCliente, 
                    count(1) as quantidadePedido, 
                    sum(co.total_pedido_com_impostos) as totalFaturado 
                from emerion_dashboard.customer_order co
                where (
                        (
                            (cast(:startDate as date) is not null or cast(:endDate as date) is not null)
                            and co.data_pedido >= coalesce(cast(:startDate as date), date '1900-01-01')
                            and co.data_pedido <= coalesce(cast(:endDate as date), current_date)
                        )
                        or (
                            cast(:startDate as date) is null
                            and cast(:endDate as date) is null
                            and (
                                cast(:years as integer) is null
                                or extract(year from co.data_pedido) >= extract(year from current_date) - cast(:years as integer) + 1
                            )
                        )
                    )
                  and co.cnpj_empresa = :cnpjEmpresa
                group by co.codigo_cliente
            )
            select 
                coalesce(c.id, tc.codigocliente),
                coalesce(c.razao_social, ''),
                tc.quantidadepedido, 
                tc.totalfaturado  
            from emerion_dashboard.customer c
            right join top_costumers tc on tc.CodigoCliente = c.external_id::int
                and c.cnpj_empresa = :cnpjEmpresa
            order by tc.${orderBy} desc
            limit 10
        """.trimIndent()
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
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

    fun getDashboardSellerTopTrends(
        orderBy: String,
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): List<DashboardTopTrendsProjection> {
        val query = entityManager.createNativeQuery(
            """
            with bounds as (
                select
                    case
                        when cast(:startDate as date) is not null
                            then cast(:startDate as date)
                        when cast(:endDate as date) is not null
                            then date '1900-01-01'
                        when cast(:years as integer) is null
                            then date '1900-01-01'
                        else make_date(extract(year from current_date)::int - cast(:years as integer) + 1, 1, 1)
                    end as start_date,
                    coalesce(cast(:endDate as date), current_date) as end_date
            ),
            top_vendedores as (
            	select 
            		co.vendedor_external_id as CodigoVendedor, 
            		count(1) as quantidadePedido, 
            		sum(co.total_pedido_com_impostos) as totalFaturado 
            	from emerion_dashboard.customer_order co
                cross join bounds b
                where co.data_pedido >= b.start_date
                  and co.data_pedido <= b.end_date
                  and co.cnpj_empresa = :cnpjEmpresa
            	group by co.vendedor_external_id 
            )
            select 
            	coalesce(v.id, tc.CodigoVendedor), 
            	coalesce(v.nome, ''), 
            	tc.quantidadepedido , 
            	tc.totalfaturado  
            from emerion_dashboard.vendedor v
            right join top_vendedores tc on tc.CodigoVendedor = v.external_id::int
                and v.cnpj_empresa = :cnpjEmpresa
            order by tc.${orderBy} desc
            limit 10
        """.trimIndent()
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
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

    fun getDashboardTotais(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): DashboardTotaisProjection {
        val query = entityManager.createNativeQuery(
            """
            with pedidos as (
                select
                    codigo_cliente,
                    vendedor_external_id,
                    total_pedido_com_impostos
                from emerion_dashboard.customer_order
                where (
                    (
                        (cast(:startDate as date) is not null or cast(:endDate as date) is not null)
                        and data_pedido >= coalesce(cast(:startDate as date), date '1900-01-01')
                        and data_pedido <= coalesce(cast(:endDate as date), current_date)
                    )
                    or (
                        cast(:startDate as date) is null
                        and cast(:endDate as date) is null
                        and (
                            cast(:years as integer) is null
                            or extract(year from data_pedido) >= extract(year from current_date) - cast(:years as integer) + 1
                        )
                    )
                )
                  and cnpj_empresa = :cnpjEmpresa
            ),
            totais_clientes as (
                select count(distinct(id)) as quantidadeClientes
                from emerion_dashboard.customer c
                right join pedidos co on c.id = co.codigo_cliente
                        and c.cnpj_empresa = :cnpjEmpresa
            ),
            totais_vendedores as (
                select count(distinct(id)) as quantidadeVendedores
                from emerion_dashboard.vendedor v
                right join pedidos co on v.external_id::int = co.vendedor_external_id
                        and v.cnpj_empresa = :cnpjEmpresa
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
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
        val resultArray = query.singleResult as Array<*>
        return DashboardTotaisProjection(
            quantidadePedidos = (resultArray[0] as Number).toInt(),
            totalFaturado = (resultArray[1] as BigDecimal),
            quantidadeClientes = (resultArray[2] as Number).toInt(),
            quantidadeVendedores = (resultArray[3] as Number).toInt()
        )
    }

    fun getCustomerDistributionByRegion(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): List<DashboardCustomerRegionProjection> {
        val query = entityManager.createNativeQuery(
            """
            select region, count(*) as customer_count
            from (
                select
                    case
                        when upper(btrim(uf)) in ('AC', 'AP', 'AM', 'PA', 'RO', 'RR', 'TO') then 'Norte'
                        when upper(btrim(uf)) in ('AL', 'BA', 'CE', 'MA', 'PB', 'PE', 'PI', 'RN', 'SE') then 'Nordeste'
                        when upper(btrim(uf)) in ('DF', 'GO', 'MT', 'MS') then 'Centro-Oeste'
                        when upper(btrim(uf)) in ('ES', 'MG', 'RJ', 'SP') then 'Sudeste'
                        when upper(btrim(uf)) in ('PR', 'RS', 'SC') then 'Sul'
                        else 'Não informado'
                    end as region
                from emerion_dashboard.customer c
                where c.cnpj_empresa = :cnpjEmpresa
                  and exists (
                      select 1
                      from emerion_dashboard.customer_order co
                      where co.cnpj_empresa = :cnpjEmpresa
                        and co.codigo_cliente = case
                            when c.external_id ~ '^[0-9]+$' then c.external_id::bigint
                            else null
                        end
                        and (
                            (
                                (cast(:startDate as date) is not null or cast(:endDate as date) is not null)
                                and co.data_pedido >= coalesce(cast(:startDate as date), date '1900-01-01')
                                and co.data_pedido <= coalesce(cast(:endDate as date), current_date)
                            )
                            or (
                                cast(:startDate as date) is null
                                and cast(:endDate as date) is null
                                and (
                                    cast(:years as integer) is null
                                    or co.data_pedido >= make_date(
                                        extract(year from current_date)::int - cast(:years as integer) + 1,
                                        1,
                                        1
                                    )
                                )
                            )
                        )
                  )
            ) customer_regions
            group by region
            order by customer_count desc, region
            """.trimIndent(),
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
        return query.resultList.map { row ->
            val resultArray = row as Array<*>
            DashboardCustomerRegionProjection(
                region = resultArray[0].toString(),
                customerCount = (resultArray[1] as Number).toInt(),
            )
        }
    }

    fun getActiveCustomerCount(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): Int {
        val query = entityManager.createNativeQuery(
            """
            select count(*)
            from emerion_dashboard.customer c
            where c.bloqueado = false
              and c.cnpj_empresa = :cnpjEmpresa
              and exists (
                  select 1
                  from emerion_dashboard.customer_order co
                  where co.cnpj_empresa = :cnpjEmpresa
                    and co.codigo_cliente = case
                        when c.external_id ~ '^[0-9]+$' then c.external_id::bigint
                        else null
                    end
                    and (
                        (
                            (cast(:startDate as date) is not null or cast(:endDate as date) is not null)
                            and co.data_pedido >= coalesce(cast(:startDate as date), date '1900-01-01')
                            and co.data_pedido <= coalesce(cast(:endDate as date), current_date)
                        )
                        or (
                            cast(:startDate as date) is null
                            and cast(:endDate as date) is null
                            and (
                                cast(:years as integer) is null
                                or co.data_pedido >= make_date(
                                    extract(year from current_date)::int - cast(:years as integer) + 1,
                                    1,
                                    1
                                )
                            )
                        )
                    )
              )
            """.trimIndent(),
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
        return (query.singleResult as Number).toInt()
    }

    fun getUnpaidInvoicesAmount(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): BigDecimal {
        val query = entityManager.createNativeQuery(
            """
            select coalesce(sum(trp.valor_parcela), 0)
            from emerion_dashboard.titulo_receber_parcela trp
            join emerion_dashboard.titulo_receber tr on tr.documento = trp.documento
            where coalesce(trp.flag_pago, '') != '*'
              and trp.data_vencimento <= now()
              and (
                (
                    (cast(:startDate as date) is not null or cast(:endDate as date) is not null)
                    and trp.data_vencimento >= coalesce(cast(:startDate as date), date '1900-01-01')
                    and trp.data_vencimento <= coalesce(cast(:endDate as date), current_date)
                )
                or (
                    cast(:startDate as date) is null
                    and cast(:endDate as date) is null
                    and (
                        cast(:years as integer) is null
                        or trp.data_vencimento >= make_date(extract(year from current_date)::int - cast(:years as integer) + 1, 1, 1)
                    )
                )
              )
              and tr.cnpj_empresa = :cnpjEmpresa
            """.trimIndent(),
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
        return query.singleResult as BigDecimal
    }

    fun getOverdueInvoices(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): List<DashboardOverdueInvoiceProjection> {
        val query = entityManager.createNativeQuery(
            """
            select
                tr.codigo_cliente,
                coalesce(tr.nome_cliente, ''),
                trp.valor_parcela,
                trp.data_vencimento,
                (current_date - trp.data_vencimento)
            from emerion_dashboard.titulo_receber tr
            join emerion_dashboard.titulo_receber_parcela trp on trp.documento = tr.documento
            where coalesce(trp.flag_pago, '') != '*'
              and trp.data_vencimento <= now()
              and (
                (
                    (cast(:startDate as date) is not null or cast(:endDate as date) is not null)
                    and trp.data_vencimento >= coalesce(cast(:startDate as date), date '1900-01-01')
                    and trp.data_vencimento <= coalesce(cast(:endDate as date), current_date)
                )
                or (
                    cast(:startDate as date) is null
                    and cast(:endDate as date) is null
                    and (
                        cast(:years as integer) is null
                        or trp.data_vencimento >= make_date(extract(year from current_date)::int - cast(:years as integer) + 1, 1, 1)
                    )
                )
              )
              and tr.cnpj_empresa = :cnpjEmpresa
            order by trp.data_vencimento, trp.id
            limit 5
            """.trimIndent(),
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
        return query.resultList.map { row ->
            val resultArray = row as Array<*>
            DashboardOverdueInvoiceProjection(
                customerCode = (resultArray[0] as? Number)?.toLong(),
                customerName = resultArray[1].toString(),
                amount = (resultArray[2] as? BigDecimal) ?: BigDecimal.ZERO,
                dueDate = resultArray[3] as java.time.LocalDate,
                daysOverdue = (resultArray[4] as Number).toInt(),
            )
        }
    }

    fun getCockpitMonthlyMetrics(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): DashboardCockpitMetricsProjection {
        val query = entityManager.createNativeQuery(
            """
            with bounds as (
                select
                    case
                        when cast(:startDate as date) is not null or cast(:endDate as date) is not null
                            then coalesce(cast(:startDate as date), date '1900-01-01')
                        when cast(:years as integer) is not null
                            then make_date(extract(year from current_date)::int - cast(:years as integer) + 1, 1, 1)
                        else date_trunc('month', current_date)::date
                    end as start_date,
                    case
                        when cast(:startDate as date) is not null or cast(:endDate as date) is not null
                            then coalesce(cast(:endDate as date), current_date) + 1
                        when cast(:years as integer) is not null
                            then current_date + 1
                        else (date_trunc('month', current_date) + interval '1 month')::date
                    end as end_date
            )
            select
                coalesce((
                    select sum(co.total_pedido_com_impostos)
                    from emerion_dashboard.customer_order co, bounds b
                    where co.data_pedido >= b.start_date and co.data_pedido < b.end_date
                      and co.cnpj_empresa = :cnpjEmpresa
                ), 0),
                coalesce((
                    select round(avg(coi.tot_ren), 2)
                    from emerion_dashboard.customer_order_item coi
                    join emerion_dashboard.customer_order co on co.id = coi.customer_order_id, bounds b
                    where co.data_pedido >= b.start_date and co.data_pedido < b.end_date
                      and co.cnpj_empresa = :cnpjEmpresa
                ), 0),
                (
                    select count(*)
                    from emerion_dashboard.customer_order co, bounds b
                    where co.data_pedido >= b.start_date and co.data_pedido < b.end_date
                      and co.cnpj_empresa = :cnpjEmpresa
                ),
                (
                    select count(*)
                    from emerion_dashboard.customer_quotation cq, bounds b
                    where cq.data_pedido >= b.start_date and cq.data_pedido < b.end_date
                      and cq.cnpj_empresa = :cnpjEmpresa
                ),
                coalesce((
                    select sum(trp.valor_parcela)
                    from emerion_dashboard.titulo_receber_parcela trp
                    join emerion_dashboard.titulo_receber tr on tr.documento = trp.documento, bounds b
                    where trp.data_vencimento >= b.start_date and trp.data_vencimento < b.end_date
                      and tr.cnpj_empresa = :cnpjEmpresa
                ), 0),
                coalesce((
                    select sum(trp.valor_parcela)
                    from emerion_dashboard.titulo_receber_parcela trp
                    join emerion_dashboard.titulo_receber tr on tr.documento = trp.documento, bounds b
                    where trp.flag_pago = '*'
                      and trp.data_vencimento >= b.start_date and trp.data_vencimento < b.end_date
                      and tr.cnpj_empresa = :cnpjEmpresa
                ), 0)
            """.trimIndent(),
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
        val row = query.singleResult as Array<*>
        return DashboardCockpitMetricsProjection(
            salesTotal = row[0] as BigDecimal,
            averageMarkup = row[1] as BigDecimal,
            customerOrders = (row[2] as Number).toInt(),
            customerQuotations = (row[3] as Number).toInt(),
            amountToReceive = row[4] as BigDecimal,
            amountReceived = row[5] as BigDecimal,
        )
    }

    fun getUnpaidInvoicesSummary(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): DashboardUnpaidInvoicesProjection {
        val query = entityManager.createNativeQuery(
            """
            select count(*), coalesce(sum(trp.valor_parcela), 0)
            from emerion_dashboard.titulo_receber_parcela trp
            join emerion_dashboard.titulo_receber tr on tr.documento = trp.documento
            where coalesce(trp.flag_pago, '') != '*'
              and trp.data_vencimento >= case
                  when cast(:startDate as date) is not null or cast(:endDate as date) is not null
                      then coalesce(cast(:startDate as date), date '1900-01-01')
                  when cast(:years as integer) is not null
                      then make_date(extract(year from current_date)::int - cast(:years as integer) + 1, 1, 1)
                  else date_trunc('month', current_date)::date
              end
              and trp.data_vencimento <= case
                  when cast(:startDate as date) is not null or cast(:endDate as date) is not null
                      then coalesce(cast(:endDate as date), current_date)
                  when cast(:years as integer) is not null
                      then current_date
                  else current_date
              end
              and tr.cnpj_empresa = :cnpjEmpresa
            """.trimIndent(),
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
        val row = query.singleResult as Array<*>
        return DashboardUnpaidInvoicesProjection(
            count = (row[0] as Number).toInt(),
            amount = row[1] as BigDecimal,
        )
    }

    fun getRecentOrders(
        limit: Int,
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): List<DashboardRecentOrderProjection> {
        val query = entityManager.createNativeQuery(
            """
            select * from (
                select
                    co.numero_pedido,
                    co.codigo_cliente,
                    coalesce(c.razao_social, '') as customer_name,
                    co.data_pedido,
                    co.status_pedido,
                    co.total_pedido_com_impostos,
                    v.nome as salesman_name,
                    coalesce((
                        select round(avg(coi.tot_ren), 2)
                        from emerion_dashboard.customer_order_item coi
                        where coi.customer_order_id = co.id
                    ), 0) as average_markup,
                    co.id
                from emerion_dashboard.customer_order co
                left join emerion_dashboard.vendedor v
                    on case
                        when v.external_id ~ '^[0-9]+$' then v.external_id::numeric
                        else null
                    end = co.vendedor_external_id
                    and v.cnpj_empresa = :cnpjEmpresa
                left join emerion_dashboard.customer c
                    on case
                        when c.external_id ~ '^[0-9]+$' then c.external_id::numeric
                        else null
                    end = co.codigo_cliente
                    and c.cnpj_empresa = :cnpjEmpresa
                where co.cnpj_empresa = :cnpjEmpresa
                  and (
                      (
                          (cast(:startDate as date) is not null or cast(:endDate as date) is not null)
                          and co.data_pedido >= coalesce(cast(:startDate as date), date '1900-01-01')
                          and co.data_pedido <= coalesce(cast(:endDate as date), current_date)
                      )
                      or (
                          cast(:startDate as date) is null
                          and cast(:endDate as date) is null
                          and (
                              cast(:years as integer) is null
                              or co.data_pedido >= make_date(
                                  extract(year from current_date)::int - cast(:years as integer) + 1,
                                  1,
                                  1
                              )
                          )
                      )
                  )
                order by (coalesce(co.status_pedido, '') = 'Faturado'), co.data_pedido desc, co.id desc
                limit :limit
            ) recent
            order by data_pedido desc, id desc
            """.trimIndent(),
        )
        query.setParameter("limit", limit)
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
        return query.resultList.map { row ->
            val resultArray = row as Array<*>
            DashboardRecentOrderProjection(
                orderNumber = resultArray[0] as String?,
                customerCode = (resultArray[1] as Number).toInt(),
                customerName = resultArray[2].toString(),
                orderDate = resultArray[3] as java.time.LocalDate,
                status = resultArray[4] as String?,
                totalAmount = resultArray[5] as BigDecimal,
                salesmanName = resultArray[6] as String?,
                averageMarkup = resultArray[7] as BigDecimal,
            )
        }
    }

    fun getCustomerMonthlyAverageTicket(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): BigDecimal {
        val query = entityManager.createNativeQuery(
            """
            with bounds as (
                select case
                    when cast(:startDate as date) is not null
                        then cast(:startDate as date)
                    when cast(:endDate as date) is not null
                        then date '1900-01-01'
                    when cast(:years as integer) is null
                        then date '1900-01-01'
                    else make_date(extract(year from current_date)::int - cast(:years as integer) + 1, 1, 1)
                end as start_date
            )
            select coalesce(round(sum(total_pedido_com_impostos) / nullif(count(*), 0), 2), 0)
            from emerion_dashboard.customer_order
            where data_pedido >= (select start_date from bounds)
              and data_pedido <= coalesce(cast(:endDate as date), current_date)
              and cnpj_empresa = :cnpjEmpresa
            """.trimIndent(),
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
        return query.singleResult as BigDecimal
    }

    fun getMostProfitableCustomers(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): List<DashboardTopTrendsProjection> {
        val query = entityManager.createNativeQuery(
            """
            with bounds as (
                select case
                    when cast(:startDate as date) is not null
                        then cast(:startDate as date)
                    when cast(:endDate as date) is not null
                        then date '1900-01-01'
                    when cast(:years as integer) is null
                        then date '1900-01-01'
                    else make_date(extract(year from current_date)::int - cast(:years as integer) + 1, 1, 1)
                end as start_date
            ),
            customer_profit as (
                select
                    co.codigo_cliente,
                    count(distinct co.id) as order_count,
                    coalesce(sum(coalesce(coi.lucro_valor, 0)), 0) as total_profit
                from emerion_dashboard.customer_order co
                join emerion_dashboard.customer_order_item coi on coi.customer_order_id = co.id
                where co.data_pedido >= (select start_date from bounds)
                  and co.data_pedido <= coalesce(cast(:endDate as date), current_date)
                  and co.cnpj_empresa = :cnpjEmpresa
                group by co.codigo_cliente
            )
            select
                coalesce(c.id, cp.codigo_cliente)::integer as identifier,
                coalesce(c.razao_social, '') as name,
                cp.order_count as amount_sales,
                cp.total_profit as total_sales
            from customer_profit cp
            left join emerion_dashboard.customer c
                on case
                    when c.external_id ~ '^[0-9]+$' then c.external_id::numeric
                    else null
                end = cp.codigo_cliente
                and c.cnpj_empresa = :cnpjEmpresa
            order by cp.total_profit desc, cp.codigo_cliente
            limit 10
            """.trimIndent(),
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
        return query.resultList.map { row ->
            val resultArray = row as Array<*>
            DashboardTopTrendsProjection(
                identifier = resultArray[0].toString(),
                name = resultArray[1].toString(),
                amountSales = (resultArray[2] as Number).toInt(),
                totalSales = resultArray[3] as BigDecimal,
            )
        }
    }

    fun getNewCustomersByMonth(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        cnpjEmpresa: String,
    ): List<DashboardNewCustomersByMonthProjection> {
        val query = entityManager.createNativeQuery(
            """
            with bounds as (
                select case
                    when cast(:startDate as date) is not null
                        then cast(:startDate as date)
                    when cast(:endDate as date) is not null
                        then date '1900-01-01'
                    when cast(:years as integer) is null
                        then coalesce((
                            select min(data_cadastro)
                            from emerion_dashboard.customer
                            where cnpj_empresa = :cnpjEmpresa
                        ), current_date)
                    else make_date(extract(year from current_date)::int - cast(:years as integer) + 1, 1, 1)
                end as start_date
            ),
            months as (
                select generate_series(
                    date_trunc('month', (select start_date from bounds)),
                    date_trunc('month', coalesce(cast(:endDate as date), current_date)),
                    interval '1 month'
                )::date as month_start
            ),
            monthly_customers as (
                select
                    date_trunc('month', data_cadastro)::date as month_start,
                    count(*) as customer_count
                from emerion_dashboard.customer
                where data_cadastro >= (select start_date from bounds)
                  and data_cadastro < (
                      date_trunc('month', coalesce(cast(:endDate as date), current_date)) + interval '1 month'
                  )::date
                  and cnpj_empresa = :cnpjEmpresa
                group by date_trunc('month', data_cadastro)::date
            )
            select
                to_char(months.month_start, 'YYYY-MM') as year_month,
                coalesce(monthly_customers.customer_count, 0) as customer_count
            from months
            left join monthly_customers on monthly_customers.month_start = months.month_start
            """.trimIndent(),
        )
        query.setParameter("years", years)
        query.setParameter("startDate", startDate)
        query.setParameter("endDate", endDate)
        query.setParameter("cnpjEmpresa", cnpjEmpresa)
        return query.resultList.map { row ->
            val resultArray = row as Array<*>
            DashboardNewCustomersByMonthProjection(
                yearMonth = resultArray[0].toString(),
                count = (resultArray[1] as Number).toInt(),
            )
        }
    }
}