package br.com.vertice.emerion_dashboard.application.product.ingestion.model

import java.math.BigDecimal

/** Input command for a single product inside an ingestion batch. */
data class IngestProductCommand(
    val externalId: String,
    val cnpjEmpresa: String,
    val nome: String,
    val descricaoReduzida: String? = null,
    val referenciaInterna: String? = null,
    val ncm: String? = null,
    val cest: String? = null,
    val origemProduto: String? = null,
    val categoria: String? = null,
    val tipo: String? = null,
    val marca: String? = null,
    val unidade: String? = null,
    val pesoLiquido: BigDecimal? = null,
    val pesoBruto: BigDecimal? = null,
    val descontinuado: Boolean? = null,
    val codigoBarras: String? = null,
    val codigoBarrasProprio: String? = null,
    val preco: BigDecimal? = null,
    val preco2: BigDecimal? = null,
    val preco3: BigDecimal? = null,
    val preco4: BigDecimal? = null,
    val preco5: BigDecimal? = null,
)
