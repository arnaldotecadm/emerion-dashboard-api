package br.com.vertice.emerion_dashboard.domain.dashboard

import java.math.BigDecimal
import java.time.LocalDate

data class OverdueInvoice(
    val customerCode: Long?,
    val customerName: String,
    val amount: BigDecimal,
    val dueDate: LocalDate,
    val daysOverdue: Int,
)
