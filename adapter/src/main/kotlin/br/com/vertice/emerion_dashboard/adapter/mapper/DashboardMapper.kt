package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardTopTrendsProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardTotaisProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardMonthlyTrendProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardOrdersByRegionProjection
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyTrend
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardOrdersByRegion
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTopTrends
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTotais
import br.com.vertice.emerion_dashboard.domain.dashboard.TopTrends
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardTopTrendsDTO
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardMonthlyCount
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardOrdersByRegion as DashboardOrdersByRegionDto
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardTopTrendsResponse

object DashboardMapper {

    fun DashboardOrdersByRegionProjection.toModel(): DashboardOrdersByRegion =
        DashboardOrdersByRegion(
            region = region,
            faturado = faturado,
            cancelado = cancelado,
            outros = outros,
        )

    fun DashboardMonthlyTrendProjection.toModel(): DashboardMonthlyTrend =
        DashboardMonthlyTrend(
            yearMonth = yearMonth,
            customerOrders = customerOrders,
            customerQuotations = customerQuotations,
        )

    fun DashboardTopTrends.toResponse(): DashboardTopTrendsResponse {
        return DashboardTopTrendsResponse(
            topCustomers = this.topCustomers.map { it.toResponse() },
            topVendedores = this.topVendedores.map { it.toResponse() },
            customerOrders = this.customerOrders.map {
                DashboardMonthlyCount(yearMonth = it.yearMonth, count = it.count)
            },
            customerQuotations = this.customerQuotations.map {
                DashboardMonthlyCount(yearMonth = it.yearMonth, count = it.count)
            },
            ordersByRegion = this.ordersByRegion.map {
                DashboardOrdersByRegionDto(
                    region = it.region,
                    faturado = it.faturado,
                    cancelado = it.cancelado,
                    outros = it.outros,
                )
            },
        )
    }

    fun TopTrends.toResponse(): DashboardTopTrendsDTO {
        return DashboardTopTrendsDTO(
            identifier = this.identifier,
            name = this.name,
            amountSales = this.amountSales,
            totalSales = this.totalSales
        )
    }

    fun DashboardTopTrendsProjection.toModel(): TopTrends {
        return TopTrends(
            identifier = this.identifier.toInt(),
            name = this.name,
            amountSales = this.amountSales,
            totalSales = this.totalSales
        )
    }

    fun DashboardTotaisProjection.toModel(): DashboardTotais {
        return DashboardTotais(
            quantidadePedidos = this.quantidadePedidos,
            quantidadeClientes = this.quantidadeClientes,
            quantidadeVendedores = this.quantidadeVendedores,
            totalFaturado = this.totalFaturado
        )
    }
}