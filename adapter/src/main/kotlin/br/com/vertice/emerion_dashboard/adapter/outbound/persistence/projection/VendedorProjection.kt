package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

/**
 * Read-side projection for `vendedor`, populated straight from a native SQL
 * result set (see `VendedorRepository`) instead of a JPA entity. Kept
 * separate from `VendedorJpaEntity` (used for writes/upserts only) so the
 * query path never pays for Hibernate's entity/session machinery.
 */
interface VendedorProjection {
    val id: Long
    val externalId: String
    val cnpjEmpresa: String
    val nome: String
    val apelido: String?
    val cpfCnpj: String?
    val telefone: String?
    val celular: String?
    val email: String?
    val cidade: String?
    val uf: String?
    val ativo: String?
    val saldo: BigDecimal?
    val dataCadastro: LocalDate?
    val createdAt: Instant
    val updatedAt: Instant
}
