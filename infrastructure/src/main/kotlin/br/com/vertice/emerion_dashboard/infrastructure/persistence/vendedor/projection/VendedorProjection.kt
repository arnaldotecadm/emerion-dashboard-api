package br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.projection

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

/**
 * Read-side projection for `vendedor`, populated straight from a native SQL
 * result set (see `VendedorQueryRepository`) instead of a JPA entity.
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
    val situacao: String?
    val saldo: BigDecimal?
    val dataCadastro: LocalDate?
    val createdAt: Instant
    val updatedAt: Instant
}
