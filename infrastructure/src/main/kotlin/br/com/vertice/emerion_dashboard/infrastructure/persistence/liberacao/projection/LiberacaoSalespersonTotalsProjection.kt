package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection

import java.math.BigDecimal

interface LiberacaoSalespersonTotalsProjection {
    val codigoVendedor: Long
    val nomeVendedor: String
    val totalFaturado: BigDecimal
    val totalComissao: BigDecimal
    val totalDespesas: BigDecimal
    val custosTotais: BigDecimal
    val totalIcms: BigDecimal
    val totalSemNota: BigDecimal
}
