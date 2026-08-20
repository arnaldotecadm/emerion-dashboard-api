package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.projection

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

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
    val dataNascimento: LocalDate?
    val dataCadastro: LocalDate?
    val dataUltimaAtualizacao: LocalDate?
    val email1: String?
    val email2: String?
    val website: String?
    val limiteCredito: BigDecimal?
    val observacoes: String?
    val cnae: String?
    val vendedorExternalId: Long?
    val nomeVendedor: String?
    val codigoTipoCliente: String?
    val codigoGrupoCliente: String?
    val codigoCategoriaCliente: String?
    val uf: String?
    val macroRegiao: String?
    val microRegiao: String?
    val setor: String?
    val createdAt: Instant
    val updatedAt: Instant
}
