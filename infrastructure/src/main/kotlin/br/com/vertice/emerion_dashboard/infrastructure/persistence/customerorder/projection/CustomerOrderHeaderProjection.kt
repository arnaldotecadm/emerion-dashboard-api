package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.projection

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

/**
 * Read-side projection for the `customer_order` header row, populated
 * straight from a native SQL result set (see
 * `CustomerOrderQueryRepository`). Line items are fetched separately via
 * `CustomerOrderItemProjection` and grouped back onto the header by
 * `id`/`customerOrderId`, mirroring emerion-load-service's
 * `CustomerOrderHeaderProjection`/`CustomerOrderItemProjection` split.
 */
interface CustomerOrderHeaderProjection {
    val id: Long
    val externalId: String
    val codCli: String
    val cnpjEmpresa: String
    val cpfCnpj: String?
    val nronfe: String?
    val dteres: LocalDate
    val sitres: String?
    val totger: BigDecimal
    val totres: BigDecimal
    val totipi: BigDecimal
    val totsub: BigDecimal
    val totdescinc: BigDecimal
    val createdAt: Instant
    val updatedAt: Instant
}
