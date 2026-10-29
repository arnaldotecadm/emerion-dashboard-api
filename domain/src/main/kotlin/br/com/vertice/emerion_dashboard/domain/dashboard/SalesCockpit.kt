package br.com.vertice.emerion_dashboard.domain.dashboard

import java.math.BigDecimal
import java.time.LocalDate

data class SalesCockpit(
    val monthlySalesTotal: BigDecimal,
    val averageMarkup: BigDecimal,
    val unpaidInvoicesCount: Int,
    val unpaidInvoicesAmount: BigDecimal,
    val customerQuotations: Int,
    val customerOrders: Int,
    val invoices: Int,
    val amountToReceiveThisMonth: BigDecimal,
    val amountReceivedThisMonth: BigDecimal,
    val amountDueThisMonth: BigDecimal,
    val recentOrders: List<RecentOrder>,
)

data class CockpitMonthlyMetrics(
    val salesTotal: BigDecimal,
    val averageMarkup: BigDecimal,
    val customerOrders: Int,
    val customerQuotations: Int,
    val amountToReceive: BigDecimal,
    val amountReceived: BigDecimal,
)

data class UnpaidInvoicesSummary(
    val count: Int,
    val amount: BigDecimal,
)

data class RecentOrder(
    val orderNumber: String?,
    val customerCode: Int,
    val customerName: String,
    val orderDate: LocalDate,
    val status: String?,
    val totalAmount: BigDecimal,
    val salesmanName: String?,
    val averageMarkup: BigDecimal,
)
