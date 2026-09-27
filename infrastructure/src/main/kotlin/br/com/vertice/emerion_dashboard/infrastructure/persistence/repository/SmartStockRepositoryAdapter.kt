package br.com.vertice.emerion_dashboard.infrastructure.persistence.repository

import br.com.vertice.emerion_dashboard.domain.dashboard.SmartStockRepository
import br.com.vertice.emerion_dashboard.domain.dashboard.StockRecommendation
import org.springframework.stereotype.Component

@Component
class SmartStockRepositoryAdapter(
    private val smartStockDataRepository: SmartStockDataRepository
) : SmartStockRepository {
    override fun getStockRecommendations(): List<StockRecommendation> {
        return smartStockDataRepository.getStockRecommendations()
    }
}