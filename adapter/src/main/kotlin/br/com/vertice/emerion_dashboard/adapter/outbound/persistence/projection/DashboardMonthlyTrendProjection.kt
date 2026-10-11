package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection

import java.math.BigDecimal

data class DashboardMonthlyTrendProjection(
    val yearMonth: String,
    val customerOrders: Int,
    val customerOrdersAmountSold: BigDecimal,
    val customerQuotations: Int,
)
