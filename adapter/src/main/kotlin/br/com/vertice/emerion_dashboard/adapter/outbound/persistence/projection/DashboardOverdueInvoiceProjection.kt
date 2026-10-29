package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection

import java.math.BigDecimal
import java.time.LocalDate

data class DashboardOverdueInvoiceProjection(
    val customerCode: Long?,
    val customerName: String,
    val amount: BigDecimal,
    val dueDate: LocalDate,
    val daysOverdue: Int,
)
