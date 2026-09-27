package br.com.vertice.emerion_dashboard.domain.dashboard

import java.math.BigDecimal

data class TopTrends(
    val identifier: Int,
    val name: String,
    val amountSales: Int,
    val totalSales: BigDecimal
)
