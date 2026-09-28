package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.application.dashboard.SmartStockService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.SmartStockApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.SmartStockDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SmartStockController(
    private val service: SmartStockService
) : SmartStockApi {
    override fun getSmartStock(): ResponseEntity<List<SmartStockDTO>> {
        return service.getStockRecommendations().let { stockRecommendations ->
            val response = stockRecommendations.map {
                SmartStockDTO(
                    codigo = it.codigo,
                    descricao = it.descricao,
                    origem = it.origem,
                    estoqueMinimo = it.estoqueMinimo,
                    estoqueAtual = it.estoqueAtual,
                    custoPonderado = it.custoPonderado,
                    custoUltimaCompra = it.custoUltimaCompra,
                    markup = it.markup,
                    leadTime = it.leadTime
                )
            }
            ResponseEntity.ok(response)
        }
    }
}