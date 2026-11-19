package br.com.vertice.emerion_dashboard.domain.liberacao.model

import java.math.BigDecimal

data class LiberacaoSalesByUf(
    val uf: String,
    val amount: BigDecimal,
    val representationPercentage: BigDecimal,
)
