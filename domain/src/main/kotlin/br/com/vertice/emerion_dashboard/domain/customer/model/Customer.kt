package br.com.vertice.emerion_dashboard.domain.customer.model

import java.time.Instant

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
    val nomeFantasia: String,
    val razaoSocial: String,
    val cpfCnpj: String,
    val inscricaoEstadual: String?,
    val regimeTributario: String?,
    val bloqueado: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        /** Factory for a brand-new customer coming from ingestion (no id yet). */
        fun newFromIngestion(
            externalId: String,
            nomeFantasia: String,
            razaoSocial: String,
            cpfCnpj: String,
            inscricaoEstadual: String?,
            regimeTributario: String?,
            bloqueado: Boolean,
            createdAt: Instant?,
            now: Instant,
        ) = Customer(
            id = null,
            externalId = externalId,
            nomeFantasia = nomeFantasia,
            razaoSocial = razaoSocial,
            cpfCnpj = cpfCnpj,
            inscricaoEstadual = inscricaoEstadual,
            regimeTributario = regimeTributario,
            bloqueado = bloqueado,
            createdAt = createdAt ?: now,
            updatedAt = now,
        )
    }

    /** Applies an ingestion update on top of an existing customer, bumping updatedAt. */
    fun mergeFromIngestion(
        nomeFantasia: String,
        razaoSocial: String,
        cpfCnpj: String,
        inscricaoEstadual: String?,
        regimeTributario: String?,
        bloqueado: Boolean,
        now: Instant,
    ) = copy(
        nomeFantasia = nomeFantasia,
        razaoSocial = razaoSocial,
        cpfCnpj = cpfCnpj,
        inscricaoEstadual = inscricaoEstadual,
        regimeTributario = regimeTributario,
        bloqueado = bloqueado,
        updatedAt = now,
    )
}
