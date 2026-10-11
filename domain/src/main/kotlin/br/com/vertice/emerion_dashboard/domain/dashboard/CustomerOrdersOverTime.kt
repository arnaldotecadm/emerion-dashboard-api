package br.com.vertice.emerion_dashboard.domain.dashboard

import java.math.BigDecimal

data class CustomerOrdersOverTime(
    val yearMonth: String,
    val orderCount: Int,
    val amountSold: BigDecimal,
)
