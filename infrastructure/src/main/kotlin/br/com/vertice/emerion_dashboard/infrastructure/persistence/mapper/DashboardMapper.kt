package br.com.vertice.emerion_dashboard.infrastructure.persistence.mapper

import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTopTrends
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTotais
import br.com.vertice.emerion_dashboard.domain.dashboard.TopTrends
import br.com.vertice.emerion_dashboard.infrastructure.persistence.projection.DashboardTopTrendsProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.projection.DashboardTotaisProjection
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardTopTrendsDTO
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardTopTrendsResponse

object DashboardMapper {

    fun DashboardTopTrendsProjection.toModel(): TopTrends {
        return TopTrends(
            identifier = this.identifier.toInt(),
            name = this.name,
            amountSales = this.amountSales,
            totalSales = this.totalSales
        )
    }

    fun DashboardTopTrends.toResponse(): DashboardTopTrendsResponse {
        return DashboardTopTrendsResponse(
            topCustomers = this.topCustomers.map { it.toResponse() },
            topVendedores = this.topVendedores.map { it.toResponse() }
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

    fun DashboardTotaisProjection.toModel(): DashboardTotais {
        return DashboardTotais(
            quantidadePedidos = this.quantidadePedidos,
            quantidadeClientes = this.quantidadeClientes,
            quantidadeVendedores = this.quantidadeVendedores,
            totalFaturado = this.totalFaturado
        )
    }
}