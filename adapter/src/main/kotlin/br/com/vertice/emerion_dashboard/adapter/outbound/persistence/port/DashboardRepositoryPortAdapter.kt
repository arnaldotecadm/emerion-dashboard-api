package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.port

import br.com.vertice.emerion_dashboard.adapter.mapper.DashboardMapper.toModel
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository.DashboardDataRepository
import br.com.vertice.emerion_dashboard.application.outbound.port.DashboardRepositoryPort
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTotais
import br.com.vertice.emerion_dashboard.domain.dashboard.TopTrends
import org.springframework.stereotype.Component

@Component
class DashboardRepositoryPortAdapter(
    private val dashboardDataRepository: DashboardDataRepository
) : DashboardRepositoryPort {
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