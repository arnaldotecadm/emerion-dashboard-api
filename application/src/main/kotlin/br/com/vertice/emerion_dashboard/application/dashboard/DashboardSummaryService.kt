package br.com.vertice.emerion_dashboard.application.dashboard

import br.com.vertice.emerion_dashboard.application.outbound.port.DashboardRepositoryPort
import br.com.vertice.emerion_dashboard.domain.dashboard.CustomerOrdersOverTime
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyCount
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyTrend
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTopTrends
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class DashboardSummaryService(
    private val repository: DashboardRepositoryPort
) : DashboardSummary {
    fun getCustomerOrdersOverTime(years: Int? = null): List<CustomerOrdersOverTime> =
        repository.getDashboardMonthlyTrends(years).map { it.toCustomerOrdersOverTime() }

    override fun getDashboardTopTrends(orderBy: String, years: Int?): DashboardTopTrends {
        val totais = repository.getDashboardTotais(years)
        val monthlyTrends = repository.getDashboardMonthlyTrends(years)
        return DashboardTopTrends(
            topCustomers = repository.getDashboardCustomerTopTrends(orderBy),
            topVendedores = repository.getDashboardSellerTopTrends(orderBy),
            customerOrders = monthlyTrends.map {
                val orderTrend = it.toCustomerOrdersOverTime()
                DashboardMonthlyCount(orderTrend.yearMonth, orderTrend.orderCount)
            },
            customerQuotations = monthlyTrends.map { DashboardMonthlyCount(it.yearMonth, it.customerQuotations) },
            ordersByRegion = repository.getDashboardOrdersByRegion(),
            quantidadePedidos = totais.quantidadePedidos,
            quantidadeClientes = totais.quantidadeClientes,
            quantidadeVendedores = totais.quantidadeVendedores,
            totalFaturado = totais.totalFaturado,
            ticketMedio = if (totais.quantidadePedidos > 0) totais.totalFaturado
                .divide(totais.quantidadePedidos.toBigDecimal(), 2, RoundingMode.HALF_UP) else BigDecimal.ZERO
        )
    }

    private fun DashboardMonthlyTrend.toCustomerOrdersOverTime() =
        CustomerOrdersOverTime(
            yearMonth = yearMonth,
            orderCount = customerOrders,
            amountSold = customerOrdersAmountSold,
        )
}