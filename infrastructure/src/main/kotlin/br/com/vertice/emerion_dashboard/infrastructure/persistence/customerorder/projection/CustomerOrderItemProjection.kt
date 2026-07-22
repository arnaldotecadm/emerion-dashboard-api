package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.projection

import java.math.BigDecimal

/**
 * Read-side projection for one `customer_order_item` row, fetched alongside
 * `CustomerOrderHeaderProjection` and grouped back onto its parent header
 * by `customerOrderId` (see `CustomerOrderQueryRepository`).
 */
interface CustomerOrderItemProjection {
    val customerOrderId: Long
    val produto: String
    val descricao: String?
    val quantidade: BigDecimal
    val valorUnitario: BigDecimal
    val valorTotal: BigDecimal
}
