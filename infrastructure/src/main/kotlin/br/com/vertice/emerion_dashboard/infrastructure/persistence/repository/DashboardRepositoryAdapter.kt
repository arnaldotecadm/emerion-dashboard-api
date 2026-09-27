package br.com.vertice.emerion_dashboard.infrastructure.persistence.repository

import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardRepository
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTotais
import br.com.vertice.emerion_dashboard.domain.dashboard.TopTrends
import br.com.vertice.emerion_dashboard.infrastructure.persistence.mapper.DashboardMapper.toModel
import org.springframework.stereotype.Component

@Component
class DashboardRepositoryAdapter(
    private val dashboardDataRepository: DashboardDataRepository
) : DashboardRepository {
    override fun getDashboardCustomerTopTrends(orderBy: String): List<TopTrends> {
        return dashboardDataRepository.getDashboardCustomerTopTrends(orderBy).map { it.toModel() }
    }

    override fun getDashboardSellerTopTrends(orderBy: String): List<TopTrends> {
        return dashboardDataRepository.getDashboardSellerTopTrends(orderBy).map { it.toModel() }
    }

    override fun getDashboardTotais(): DashboardTotais {
        return dashboardDataRepository.getDashboardTotais().toModel()
    }
}