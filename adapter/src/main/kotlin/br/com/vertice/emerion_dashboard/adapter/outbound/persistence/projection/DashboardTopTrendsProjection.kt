package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection

import java.math.BigDecimal

data class DashboardTopTrendsProjection(
    val identifier: String,
    val name: String,
    val amountSales: Int,
    val totalSales: BigDecimal
)