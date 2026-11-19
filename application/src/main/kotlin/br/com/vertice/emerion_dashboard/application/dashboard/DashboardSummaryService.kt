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
import java.time.LocalDate

@Service
class DashboardSummaryService(
    private val repository: DashboardRepositoryPort
) : DashboardSummary {
    fun getCustomerOrdersOverTime(
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): List<CustomerOrdersOverTime> =
        repository.getDashboardMonthlyTrends(years, startDate, endDate).map { it.toCustomerOrdersOverTime() }

    fun getSellerTopTrends(
        orderBy: String,
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): List<TopTrends> =
        repository.getDashboardSellerTopTrends(orderBy, years, startDate, endDate)

    fun getSalesCockpit(
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): SalesCockpit {
        val metrics = repository.getCockpitMonthlyMetrics(years, startDate, endDate)
        val unpaid = repository.getUnpaidInvoicesSummary(years, startDate, endDate)
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
            recentOrders = repository.getRecentOrders(RECENT_ORDERS_LIMIT, years, startDate, endDate),
        )
    }

    fun getCustomerTopTrends(
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): CustomerTopTrends =
        CustomerTopTrends(
            customerDistributionByRegion = repository.getCustomerDistributionByRegion(years, startDate, endDate),
            activeCustomers = repository.getActiveCustomerCount(years, startDate, endDate),
            unpaidInvoicesAmount = repository.getUnpaidInvoicesAmount(years, startDate, endDate),
            monthlyAverageTicket = repository.getCustomerMonthlyAverageTicket(years, startDate, endDate),
            mostProfitableCustomers = repository.getMostProfitableCustomers(years, startDate, endDate),
            newCustomersByMonth = repository.getNewCustomersByMonth(years, startDate, endDate),
            overdueInvoices = repository.getOverdueInvoices(years, startDate, endDate),
        )

    override fun getDashboardTopTrends(
        orderBy: String,
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): DashboardTopTrends {
        val totais = repository.getDashboardTotais(years, startDate, endDate)
        val monthlyTrends = repository.getDashboardMonthlyTrends(years, startDate, endDate)
        return DashboardTopTrends(
            topCustomers = repository.getDashboardCustomerTopTrends(orderBy, years, startDate, endDate),
            topVendedores = repository.getDashboardSellerTopTrends(orderBy, years, startDate, endDate),
            customerOrders = monthlyTrends.map {
                val orderTrend = it.toCustomerOrdersOverTime()
                DashboardMonthlyCount(orderTrend.yearMonth, orderTrend.orderCount)
            },
            customerQuotations = monthlyTrends.map { DashboardMonthlyCount(it.yearMonth, it.customerQuotations) },
            ordersByRegion = repository.getDashboardOrdersByRegion(years, startDate, endDate),
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
