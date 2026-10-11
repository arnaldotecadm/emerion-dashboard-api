package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection

import java.math.BigDecimal

data class DashboardOrdersByRegionProjection(
    val region: String,
    val faturado: BigDecimal,
    val cancelado: BigDecimal,
    val outros: BigDecimal,
)
