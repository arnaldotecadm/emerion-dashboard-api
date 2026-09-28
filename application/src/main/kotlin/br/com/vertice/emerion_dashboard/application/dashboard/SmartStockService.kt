package br.com.vertice.emerion_dashboard.application.dashboard

import br.com.vertice.emerion_dashboard.application.outbound.port.SmartStockRepositoryPort
import br.com.vertice.emerion_dashboard.domain.dashboard.StockRecommendation
import org.springframework.stereotype.Service

@Service
class SmartStockService(
    private val repository: SmartStockRepositoryPort
) {
    fun getStockRecommendations(): List<StockRecommendation> {
        return repository.getStockRecommendations()
    }
}