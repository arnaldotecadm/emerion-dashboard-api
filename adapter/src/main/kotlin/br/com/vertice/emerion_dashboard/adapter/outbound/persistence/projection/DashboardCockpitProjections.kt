package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection

import java.math.BigDecimal
import java.time.LocalDate

data class DashboardCockpitMetricsProjection(
    val salesTotal: BigDecimal,
    val averageMarkup: BigDecimal,
    val customerOrders: Int,
    val customerQuotations: Int,
    val amountToReceive: BigDecimal,
    val amountReceived: BigDecimal,
)

data class DashboardUnpaidInvoicesProjection(
    val count: Int,
    val amount: BigDecimal,
)

data class DashboardRecentOrderProjection(
    val orderNumber: String?,
    val customerCode: Int,
    val customerName: String,
    val orderDate: LocalDate,
    val status: String?,
    val totalAmount: BigDecimal,
    val salesmanName: String?,
    val averageMarkup: BigDecimal,
)
