package br.com.vertice.emerion_dashboard.infrastructure.persistence.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.projection.DashboardTopTrendsProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.projection.DashboardTotaisProjection
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class DashboardDataRepository(
    @PersistenceContext
    private val entityManager: EntityManager
) {

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
                c.id, 
                c.razao_social, 
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
            	v.id, 
            	v.nome, 
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
                sum(co.total_pedido_com_impostos) totalFaturado,
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