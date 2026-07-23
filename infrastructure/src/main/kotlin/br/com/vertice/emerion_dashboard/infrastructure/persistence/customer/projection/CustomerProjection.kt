package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.projection

import java.time.Instant

/**
 * Read-side projection for `customer`, populated straight from a native SQL
 * result set (see `CustomerQueryRepository`) instead of a JPA entity. Kept
 * separate from `CustomerJpaEntity` (used for writes/upserts only) so the
 * query path never pays for Hibernate's entity/session machinery.
 */
interface CustomerProjection {
    val id: Long
    val externalId: String
    val cnpjEmpresa: String
    val nomeFantasia: String
    val razaoSocial: String
    val cpfCnpj: String
    val inscricaoEstadual: String?
    val regimeTributario: String?
    val bloqueado: Boolean
    val createdAt: Instant
    val updatedAt: Instant
}
