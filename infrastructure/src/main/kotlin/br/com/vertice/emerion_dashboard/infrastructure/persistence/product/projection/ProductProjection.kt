package br.com.vertice.emerion_dashboard.infrastructure.persistence.product.projection

import java.math.BigDecimal
import java.time.Instant

/**
 * Read-side projection for `product`, populated straight from a native SQL
 * result set (see `ProductQueryRepository`) instead of a JPA entity.
 */
interface ProductProjection {
    val id: Long
    val externalId: String
    val cnpjEmpresa: String
    val nome: String
    val preco: BigDecimal?
    val createdAt: Instant
    val updatedAt: Instant
}
