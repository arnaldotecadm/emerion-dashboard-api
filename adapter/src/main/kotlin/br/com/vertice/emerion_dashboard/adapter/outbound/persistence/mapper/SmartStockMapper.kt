package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.mapper

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.StockRecommendationProjection
import br.com.vertice.emerion_dashboard.domain.dashboard.StockRecommendation

object SmartStockMapper {

    fun StockRecommendationProjection.toDomain(): StockRecommendation {
        return StockRecommendation(
            codigo = codigo,
            descricao = descricao,
            origem = origem,
            estoqueMinimo = estoqueMinimo,
            estoqueAtual = estoqueAtual,
            custoPonderado = custoPonderado,
            custoUltimaCompra = custoUltimaCompra,
            markup = markup,
            leadTime = leadTime
        )
    }
}