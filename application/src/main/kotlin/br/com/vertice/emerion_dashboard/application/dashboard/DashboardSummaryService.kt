package br.com.vertice.emerion_dashboard.application.dashboard

import br.com.vertice.emerion_dashboard.application.outbound.port.DashboardRepositoryPort
import br.com.vertice.emerion_dashboard.domain.dashboard.CustomerOrdersOverTime
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyCount
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyTrend
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTopTrends
import br.com.vertice.emerion_dashboard.domain.dashboard.CustomerTopTrends
import br.com.vertice.emerion_dashboard.domain.dashboard.SalesCockpit
import br.com.vertice.emerion_dashboard.domain.dashboard.TopTrends
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class DashboardSummaryService(
    private val repository: DashboardRepositoryPort
) : DashboardSummary {
    fun getCustomerOrdersOverTime(years: Int? = null): List<CustomerOrdersOverTime> =
        repository.getDashboardMonthlyTrends(years).map { it.toCustomerOrdersOverTime() }

    fun getSellerTopTrends(orderBy: String, years: Int? = null): List<TopTrends> =
        repository.getDashboardSellerTopTrends(orderBy, years)

    fun getSalesCockpit(): SalesCockpit {
        val metrics = repository.getCockpitMonthlyMetrics()
        val unpaid = repository.getUnpaidInvoicesSummary()
        return SalesCockpit(
            monthlySalesTotal = metrics.salesTotal,
            averageMarkup = metrics.averageMarkup,
            unpaidInvoicesCount = unpaid.count,
            unpaidInvoicesAmount = unpaid.amount,
            customerQuotations = metrics.customerQuotations,
            customerOrders = metrics.customerOrders,
            invoices = 0,
            amountToReceiveThisMonth = metrics.amountToReceive,
            amountReceivedThisMonth = metrics.amountReceived,
            amountDueThisMonth = metrics.amountToReceive - metrics.amountReceived,
            recentOrders = repository.getRecentOrders(RECENT_ORDERS_LIMIT),
        )
    }

    fun getCustomerTopTrends(years: Int? = null): CustomerTopTrends =
        CustomerTopTrends(
            customerDistributionByRegion = repository.getCustomerDistributionByRegion(),
            activeCustomers = repository.getActiveCustomerCount(),
            unpaidInvoicesAmount = repository.getUnpaidInvoicesAmount(years),
            monthlyAverageTicket = repository.getCustomerMonthlyAverageTicket(years),
            mostProfitableCustomers = repository.getMostProfitableCustomers(years),
            newCustomersByMonth = repository.getNewCustomersByMonth(years),
            overdueInvoices = repository.getOverdueInvoices(years),
        )

    override fun getDashboardTopTrends(orderBy: String, years: Int?): DashboardTopTrends {
        val totais = repository.getDashboardTotais(years)
        val monthlyTrends = repository.getDashboardMonthlyTrends(years)
        return DashboardTopTrends(
            topCustomers = repository.getDashboardCustomerTopTrends(orderBy),
            topVendedores = repository.getDashboardSellerTopTrends(orderBy, years),
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

private const val RECENT_ORDERS_LIMIT = 10
