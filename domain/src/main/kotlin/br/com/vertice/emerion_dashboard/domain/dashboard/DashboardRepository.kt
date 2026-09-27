package br.com.vertice.emerion_dashboard.domain.dashboard

interface DashboardRepository {
    fun getDashboardCustomerTopTrends(orderBy: String): List<TopTrends>
    fun getDashboardSellerTopTrends(orderBy: String): List<TopTrends>
    fun getDashboardTotais(): DashboardTotais
}