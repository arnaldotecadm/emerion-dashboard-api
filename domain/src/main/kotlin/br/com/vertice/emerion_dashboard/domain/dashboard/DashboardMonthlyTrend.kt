package br.com.vertice.emerion_dashboard.domain.dashboard

import java.math.BigDecimal

data class DashboardMonthlyTrend(
    val yearMonth: String,
    val customerOrders: Int,
    val customerOrdersAmountSold: BigDecimal,
    val customerQuotations: Int,
)
