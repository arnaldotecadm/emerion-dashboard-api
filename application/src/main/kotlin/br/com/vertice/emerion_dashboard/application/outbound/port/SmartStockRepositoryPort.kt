package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.dashboard.StockRecommendation

interface SmartStockRepositoryPort {
    fun getStockRecommendations(): List<StockRecommendation>
}