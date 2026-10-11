package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection

data class DashboardMonthlyTrendProjection(
    val yearMonth: String,
    val customerOrders: Int,
    val customerQuotations: Int,
)
