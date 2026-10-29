package br.com.vertice.emerion_dashboard.domain.dashboard

import java.math.BigDecimal

data class CustomerTopTrends(
    val customerDistributionByRegion: List<CustomerRegionDistribution>,
    val activeCustomers: Int,
    val unpaidInvoicesAmount: BigDecimal,
    val monthlyAverageTicket: BigDecimal,
    val mostProfitableCustomers: List<TopTrends>,
    val newCustomersByMonth: List<DashboardMonthlyCount>,
    val overdueInvoices: List<OverdueInvoice>,
)
