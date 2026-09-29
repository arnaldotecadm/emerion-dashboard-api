package br.com.vertice.emerion_dashboard.application.dashboard

import br.com.vertice.emerion_dashboard.application.outbound.port.DashboardRepositoryPort
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTopTrends
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class DashboardSummaryService(
    private val repository: DashboardRepositoryPort
) : DashboardSummary {
    override fun getDashboardTopTrends(orderBy: String): DashboardTopTrends {
        val totais = repository.getDashboardTotais()
        return DashboardTopTrends(
            topCustomers = repository.getDashboardCustomerTopTrends(orderBy),
            topVendedores = repository.getDashboardSellerTopTrends(orderBy),
            quantidadePedidos = totais.quantidadePedidos,
            quantidadeClientes = totais.quantidadeClientes,
            quantidadeVendedores = totais.quantidadeVendedores,
            totalFaturado = totais.totalFaturado,
            ticketMedio = if (totais.quantidadePedidos > 0) totais.totalFaturado
                .divide(totais.quantidadePedidos.toBigDecimal(), 2, RoundingMode.HALF_UP) else BigDecimal.ZERO
        )
    }
}