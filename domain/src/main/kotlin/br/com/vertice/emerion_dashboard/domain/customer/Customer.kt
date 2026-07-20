package br.com.vertice.emerion_dashboard.domain.customer

import java.time.Instant

enum class CustomerStatus {
    ACTIVE,
    INACTIVE,
    UNKNOWN,
}

/**
 * Domain model for a Customer. Plain Kotlin, no JPA/Jakarta/OpenAPI
 * annotations — this is what use cases and ports operate on. Persistence
 * (JPA entity) and REST (generated DTOs) each have their own mapper to/from
 * this type, so changes to the database schema or the API contract never
 * force changes here (and vice versa).
 */
data class Customer(
    val id: Long?,
    val externalId: String,
    val name: String,
    val email: String?,
    val status: CustomerStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        /** Factory for a brand-new customer coming from ingestion (no id yet). */
        fun newFromIngestion(
            externalId: String,
            name: String,
            email: String?,
            status: CustomerStatus,
            createdAt: Instant?,
            now: Instant,
        ) = Customer(
            id = null,
            externalId = externalId,
            name = name,
            email = email,
            status = status,
            createdAt = createdAt ?: now,
            updatedAt = now,
        )
    }

    /** Applies an ingestion update on top of an existing customer, bumping updatedAt. */
    fun mergeFromIngestion(
        name: String,
        email: String?,
        status: CustomerStatus,
        now: Instant,
    ) = copy(name = name, email = email, status = status, updatedAt = now)
}
