package br.com.vertice.emerion_dashboard.domain.liberacao.model

import java.math.BigDecimal

data class LiberacaoSalespersonTotals(
    val codigoVendedor: Long,
    val nomeVendedor: String,
    val totalFaturado: BigDecimal,
    val totalComissao: BigDecimal,
    val totalDespesas: BigDecimal,
    val custosTotais: BigDecimal,
    val totalIcms: BigDecimal,
    val totalSemNota: BigDecimal,
)
