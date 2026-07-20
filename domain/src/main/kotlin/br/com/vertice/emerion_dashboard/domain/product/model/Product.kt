package br.com.vertice.emerion_dashboard.domain.product.model

import java.math.BigDecimal
import java.time.Instant

/**
 * Domain model for a Product. Plain Kotlin, no JPA/Jakarta/OpenAPI
 * annotations — this is what use cases and ports operate on. Persistence
 * (JPA entity) and REST (generated DTOs) each have their own mapper to/from
 * this type, so changes to the database schema or the API contract never
 * force changes here (and vice versa).
 */
data class Product(
    val id: Long?,
    val externalId: String,
    val nome: String,
    val preco: BigDecimal?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        /** Factory for a brand-new product coming from ingestion (no id yet). */
        fun newFromIngestion(
            externalId: String,
            nome: String,
            preco: BigDecimal?,
            now: Instant,
        ) = Product(
            id = null,
            externalId = externalId,
            nome = nome,
            preco = preco,
            createdAt = now,
            updatedAt = now,
        )
    }

    /** Applies an ingestion update on top of an existing product, bumping updatedAt. */
    fun mergeFromIngestion(
        nome: String,
        preco: BigDecimal?,
        now: Instant,
    ) = copy(
        nome = nome,
        preco = preco,
        updatedAt = now,
    )
}
