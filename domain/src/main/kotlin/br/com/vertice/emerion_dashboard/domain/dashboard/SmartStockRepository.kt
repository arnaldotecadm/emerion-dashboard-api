package br.com.vertice.emerion_dashboard.domain.dashboard

interface SmartStockRepository {
    fun getStockRecommendations(): List<StockRecommendation>
}