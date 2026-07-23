package br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.projection

import java.math.BigDecimal
import java.time.Instant

/**
 * Read-side projection for `customer_credit`, populated straight from a
 * native SQL result set (see `CustomerCreditQueryRepository`) instead of a
 * JPA entity.
 */
interface CustomerCreditProjection {
    val id: Long
    val customerExternalId: String
    val cnpjEmpresa: String
    val sequencia: String?
    val data: Instant
    val dataPedido: Instant?
    val valorUtilizado: BigDecimal
    val valorTotal: BigDecimal
    val saldo: BigDecimal
    val situacao: String?
    val tipo: String
    val createdAt: Instant
    val updatedAt: Instant
}
