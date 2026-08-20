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
    val unidadeEntrada: String?
    val unidadeSaida: String?
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
    val descontoPadrao: BigDecimal?
    val estoqueDisponivel: BigDecimal?
    val estoqueMinimo: BigDecimal?
    val estoqueMaximo: BigDecimal?
    val estoqueReservado: BigDecimal?
    val estoqueAdquirido: BigDecimal?
    val estoqueAtual: BigDecimal?
    val estoqueRma: BigDecimal?
    val similarProduct: String?
    val quantidadeVolumes: BigDecimal?
    val quantidadeEmbalagem: BigDecimal?
    val localizacao: String?
    val cubagem: BigDecimal?
    val codigoBarrasEmbalagem: String?
    val ibsCClassTrib: String?
    val ibsCst: String?
    val fcpEntrada: BigDecimal?
    val fcpSaida: BigDecimal?
    val ipiSaida: String?
    val ipiEntrada: String?
    val icmSaida: String?
    val icmEntrada: String?
    val icmStSaida: String?
    val icmStEntrada: String?
    val observacao: String?
    val createdAt: Instant
    val updatedAt: Instant
}
