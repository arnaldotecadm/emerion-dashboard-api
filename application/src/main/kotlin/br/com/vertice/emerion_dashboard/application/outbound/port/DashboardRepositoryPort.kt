package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTotais
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyCount
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyTrend
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardOrdersByRegion
import br.com.vertice.emerion_dashboard.domain.dashboard.CustomerRegionDistribution
import br.com.vertice.emerion_dashboard.domain.dashboard.TopTrends
import java.math.BigDecimal
import java.time.LocalDate

interface DashboardRepositoryPort {
    fun getDashboardCustomerTopTrends(
        orderBy: String,
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): List<TopTrends>
    fun getDashboardSellerTopTrends(
        orderBy: String,
        years: Int?,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): List<TopTrends>
    fun getDashboardMonthlyTrends(
        years: Int?,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): List<DashboardMonthlyTrend>
    fun getDashboardOrdersByRegion(
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): List<DashboardOrdersByRegion>
    fun getDashboardTotais(
        years: Int?,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): DashboardTotais
    fun getCustomerDistributionByRegion(
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): List<CustomerRegionDistribution>
    fun getActiveCustomerCount(
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): Int
    fun getUnpaidInvoicesAmount(
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): java.math.BigDecimal
    fun getCustomerMonthlyAverageTicket(
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): BigDecimal
    fun getMostProfitableCustomers(
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): List<TopTrends>
    fun getNewCustomersByMonth(
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): List<DashboardMonthlyCount>
    fun getCockpitMonthlyMetrics(): br.com.vertice.emerion_dashboard.domain.dashboard.CockpitMonthlyMetrics
    fun getUnpaidInvoicesSummary(): br.com.vertice.emerion_dashboard.domain.dashboard.UnpaidInvoicesSummary
    fun getRecentOrders(limit: Int): List<br.com.vertice.emerion_dashboard.domain.dashboard.RecentOrder>
    fun getOverdueInvoices(
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): List<br.com.vertice.emerion_dashboard.domain.dashboard.OverdueInvoice>
}