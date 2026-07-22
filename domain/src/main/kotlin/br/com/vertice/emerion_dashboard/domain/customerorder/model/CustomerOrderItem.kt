package br.com.vertice.emerion_dashboard.domain.customerorder.model

import java.math.BigDecimal

/**
 * One line item of a CustomerOrder. A value object with no id of its own —
 * it is always replaced as a whole list on re-ingestion (see
 * CustomerOrder.mergeFromIngestion), keyed by `produto` within its parent.
 */
data class CustomerOrderItem(
    val produto: String,
    val descricao: String?,
    val quantidade: BigDecimal,
    val valorUnitario: BigDecimal,
    val valorTotal: BigDecimal,
)
