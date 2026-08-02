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
    val descricaoReduzida: String?
    val referenciaInterna: String?
    val ncm: String?
    val cest: String?
    val origemProduto: String?
    val categoria: String?
    val tipo: String?
    val marca: String?
    val unidade: String?
    val pesoLiquido: BigDecimal?
    val pesoBruto: BigDecimal?
    val descontinuado: Boolean?
    val codigoBarras: String?
    val codigoBarrasProprio: String?
    val preco: BigDecimal?
    val preco2: BigDecimal?
    val preco3: BigDecimal?
    val preco4: BigDecimal?
    val preco5: BigDecimal?
    val createdAt: Instant
    val updatedAt: Instant
}
