package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTotais
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyCount
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyTrend
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardOrdersByRegion
import br.com.vertice.emerion_dashboard.domain.dashboard.CustomerRegionDistribution
import br.com.vertice.emerion_dashboard.domain.dashboard.TopTrends
import java.math.BigDecimal

interface DashboardRepositoryPort {
    fun getDashboardCustomerTopTrends(orderBy: String): List<TopTrends>
    fun getDashboardSellerTopTrends(orderBy: String, years: Int?): List<TopTrends>
    fun getDashboardMonthlyTrends(years: Int?): List<DashboardMonthlyTrend>
    fun getDashboardOrdersByRegion(): List<DashboardOrdersByRegion>
    fun getDashboardTotais(years: Int?): DashboardTotais
    fun getCustomerDistributionByRegion(): List<CustomerRegionDistribution>
    fun getActiveCustomerCount(): Int
    fun getUnpaidInvoicesAmount(years: Int? = null): java.math.BigDecimal
    fun getCustomerMonthlyAverageTicket(years: Int? = null): BigDecimal
    fun getMostProfitableCustomers(years: Int? = null): List<TopTrends>
    fun getNewCustomersByMonth(years: Int? = null): List<DashboardMonthlyCount>
    fun getOverdueInvoices(years: Int? = null): List<br.com.vertice.emerion_dashboard.domain.dashboard.OverdueInvoice>
}