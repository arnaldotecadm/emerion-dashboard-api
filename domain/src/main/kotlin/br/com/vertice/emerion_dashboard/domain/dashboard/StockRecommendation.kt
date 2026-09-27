package br.com.vertice.emerion_dashboard.domain.dashboard

import java.math.BigDecimal

data class StockRecommendation(
    val codigo: String,
    val descricao: String,
    val origem: String,
    val estoqueMinimo: Double,
    val estoqueAtual: Double,
    val custoPonderado: BigDecimal,
    val custoUltimaCompra: BigDecimal,
    val markup: Double,
    val leadTime: Double
)
