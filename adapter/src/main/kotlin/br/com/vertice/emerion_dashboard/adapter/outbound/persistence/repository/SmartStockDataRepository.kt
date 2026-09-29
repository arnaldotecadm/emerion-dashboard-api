package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.StockRecommendationProjection
import br.com.vertice.emerion_dashboard.domain.dashboard.StockRecommendation
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class SmartStockDataRepository(
    @PersistenceContext
    private val entityManager: EntityManager
) {

    fun getStockRecommendations(): List<StockRecommendationProjection> {
        val querySql = """
            select 
                p.external_id as codigo,
                p.nome as nome,
                p.origem_produto as origem,
                coalesce(p.estoque_atual, 0) as estoqueAtual,
                coalesce(p.estoque_minimo, 0) as estoqueMinimo,
                coalesce(p.custo_ponderado, 0) as custoPonderado,
                coalesce(p.custo_ultima_compra, 0) as custoUltimaCompra,
                coalesce(p.markup, 0) as markup,
                0 as leadTime
            from emerion_dashboard.product p
            order by p.estoque_atual
            limit 100
        """.trimIndent()

        val query = entityManager.createNativeQuery(querySql)
        return query.resultList.map { row ->
            val resultArray = row as Array<*>
            StockRecommendationProjection(
                codigo = resultArray[0].toString(),
                descricao = resultArray[1].toString(),
                origem = resultArray[2].toString(),
                estoqueAtual = (resultArray[3] as Number).toDouble(),
                estoqueMinimo = (resultArray[4] as Number).toDouble(),
                custoPonderado = (resultArray[5] as BigDecimal),
                custoUltimaCompra = (resultArray[6] as BigDecimal),
                markup = (resultArray[7] as Number).toDouble(),
                leadTime = (resultArray[8] as Number).toDouble()
            )
        }
    }
}