package br.com.vertice.emerion_dashboard.domain.dashboard

import java.math.BigDecimal

data class DashboardOrdersByRegion(
    val region: String,
    val faturado: BigDecimal,
    val cancelado: BigDecimal,
    val outros: BigDecimal,
)
