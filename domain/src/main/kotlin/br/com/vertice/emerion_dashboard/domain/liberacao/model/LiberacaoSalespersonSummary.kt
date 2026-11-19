package br.com.vertice.emerion_dashboard.domain.liberacao.model

import java.math.BigDecimal

data class LiberacaoSalespersonSummary(
    val codigoVendedor: Long,
    val nomeVendedor: String,
    val clientesResponsaveis: Int,
    val clientesComCompra: Int,
    val representationPercentage: BigDecimal,
)
