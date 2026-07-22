package br.com.vertice.emerion_dashboard.domain.vendedor.model

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

/**
 * Domain model for a Vendedor (sales representative). Plain Kotlin, no
 * JPA/Jakarta/OpenAPI annotations — this is what use cases and ports
 * operate on. Persistence (JPA entity) and REST (generated DTOs) each have
 * their own mapper to/from this type, so changes to the database schema or
 * the API contract never force changes here (and vice versa).
 */
data class Vendedor(
    val id: Long?,
    val externalId: String,
    val nome: String,
    val apelido: String?,
    val cpfCnpj: String?,
    val telefone: String?,
    val celular: String?,
    val email: String?,
    val cidade: String?,
    val uf: String?,
    val situacao: String?,
    val saldo: BigDecimal?,
    val dataCadastro: LocalDate?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        /** Factory for a brand-new vendedor coming from ingestion (no id yet). */
        fun newFromIngestion(
            externalId: String,
            nome: String,
            apelido: String?,
            cpfCnpj: String?,
            telefone: String?,
            celular: String?,
            email: String?,
            cidade: String?,
            uf: String?,
            situacao: String?,
            saldo: BigDecimal?,
            dataCadastro: LocalDate?,
            now: Instant,
        ) = Vendedor(
            id = null,
            externalId = externalId,
            nome = nome,
            apelido = apelido,
            cpfCnpj = cpfCnpj,
            telefone = telefone,
            celular = celular,
            email = email,
            cidade = cidade,
            uf = uf,
            situacao = situacao,
            saldo = saldo,
            dataCadastro = dataCadastro,
            createdAt = now,
            updatedAt = now,
        )
    }

    /** Applies an ingestion update on top of an existing vendedor, bumping updatedAt. */
    fun mergeFromIngestion(
        nome: String,
        apelido: String?,
        cpfCnpj: String?,
        telefone: String?,
        celular: String?,
        email: String?,
        cidade: String?,
        uf: String?,
        situacao: String?,
        saldo: BigDecimal?,
        dataCadastro: LocalDate?,
        now: Instant,
    ) = copy(
        nome = nome,
        apelido = apelido,
        cpfCnpj = cpfCnpj,
        telefone = telefone,
        celular = celular,
        email = email,
        cidade = cidade,
        uf = uf,
        situacao = situacao,
        saldo = saldo,
        dataCadastro = dataCadastro,
        updatedAt = now,
    )
}
