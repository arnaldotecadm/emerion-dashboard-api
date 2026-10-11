package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTotais
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyTrend
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardOrdersByRegion
import br.com.vertice.emerion_dashboard.domain.dashboard.TopTrends

interface DashboardRepositoryPort {
    fun getDashboardCustomerTopTrends(orderBy: String): List<TopTrends>
    fun getDashboardSellerTopTrends(orderBy: String): List<TopTrends>
    fun getDashboardMonthlyTrends(): List<DashboardMonthlyTrend>
    fun getDashboardOrdersByRegion(): List<DashboardOrdersByRegion>
    fun getDashboardTotais(): DashboardTotais
}