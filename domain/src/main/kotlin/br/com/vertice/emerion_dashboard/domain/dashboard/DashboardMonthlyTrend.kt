package br.com.vertice.emerion_dashboard.domain.dashboard

data class DashboardMonthlyTrend(
    val yearMonth: String,
    val customerOrders: Int,
    val customerQuotations: Int,
)
