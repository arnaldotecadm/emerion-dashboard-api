package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection

import java.math.BigDecimal

interface LiberacaoSalespersonSummaryProjection {
    val codigoVendedor: Long
    val nomeVendedor: String
    val clientesResponsaveis: Int
    val clientesComCompra: Int
    val representationPercentage: BigDecimal
}
