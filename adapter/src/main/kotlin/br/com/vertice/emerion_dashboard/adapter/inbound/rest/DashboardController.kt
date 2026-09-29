package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.DashboardMapper.toResponse
import br.com.vertice.emerion_dashboard.application.dashboard.DashboardSummaryService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.DashboardApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardTopTrendsResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class DashboardController(
    private val dashboardSummaryService: DashboardSummaryService
) : DashboardApi {
    override fun getDashboardTopTrends(orderby: String): ResponseEntity<DashboardTopTrendsResponse> {
        dashboardSummaryService.getDashboardTopTrends(orderby).let { topTrends ->
            val response = DashboardTopTrendsResponse(
                topCustomers = topTrends.topCustomers.map { it.toResponse() },
                topVendedores = topTrends.topVendedores.map { it.toResponse() },
                quantidadePedidos = topTrends.quantidadePedidos,
                totalFaturado = topTrends.totalFaturado,
                quantidadeClientes = topTrends.quantidadeClientes,
                quantidadeVendedores = topTrends.quantidadeVendedores,
                ticketMedio = topTrends.ticketMedio
            )
            return ResponseEntity.ok(response)
        }
    }
}