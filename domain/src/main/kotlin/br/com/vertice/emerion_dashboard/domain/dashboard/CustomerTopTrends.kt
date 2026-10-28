package br.com.vertice.emerion_dashboard.domain.dashboard

import java.math.BigDecimal

data class CustomerTopTrends(
    val customerDistributionByRegion: List<CustomerRegionDistribution>,
    val activeCustomers: Int,
    val monthlyAverageTicket: BigDecimal,
    val mostProfitableCustomers: List<TopTrends>,
    val newCustomersByMonth: List<DashboardMonthlyCount>,
)
