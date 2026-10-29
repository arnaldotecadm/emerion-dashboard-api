package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.port

import br.com.vertice.emerion_dashboard.adapter.mapper.DashboardMapper.toModel
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository.DashboardDataRepository
import br.com.vertice.emerion_dashboard.application.cognitouser.UserService
import br.com.vertice.emerion_dashboard.application.outbound.port.DashboardRepositoryPort
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTotais
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyTrend
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardOrdersByRegion
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyCount
import br.com.vertice.emerion_dashboard.domain.dashboard.CustomerRegionDistribution
import br.com.vertice.emerion_dashboard.domain.dashboard.TopTrends
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class DashboardRepositoryPortAdapter(
    private val dashboardDataRepository: DashboardDataRepository,
    private val userService: UserService
) : DashboardRepositoryPort {
    override fun getDashboardCustomerTopTrends(orderBy: String): List<TopTrends> {
        return dashboardDataRepository.getDashboardCustomerTopTrends(orderBy).map { it.toModel() }
    }

    override fun getDashboardSellerTopTrends(orderBy: String, years: Int?): List<TopTrends> {
        return dashboardDataRepository.getDashboardSellerTopTrends(orderBy, years).map { it.toModel() }
    }

    override fun getDashboardMonthlyTrends(years: Int?): List<DashboardMonthlyTrend> =
        dashboardDataRepository.getDashboardMonthlyTrends(years).map { it.toModel() }

    override fun getDashboardOrdersByRegion(): List<DashboardOrdersByRegion> =
        dashboardDataRepository.getDashboardOrdersByRegion().map { it.toModel() }

    override fun     getDashboardTotais(years: Int?): DashboardTotais {
            return dashboardDataRepository.getDashboardTotais(years).toModel()
    }

    override fun getCustomerDistributionByRegion(): List<CustomerRegionDistribution> =
        dashboardDataRepository.getCustomerDistributionByRegion().map { it.toModel() }

    override fun getActiveCustomerCount(): Int = dashboardDataRepository.getActiveCustomerCount()

    override fun getUnpaidInvoicesAmount(years: Int?) = dashboardDataRepository.getUnpaidInvoicesAmount(years)

    override fun getCustomerMonthlyAverageTicket(years: Int?): BigDecimal =
        dashboardDataRepository.getCustomerMonthlyAverageTicket(years)

    override fun getMostProfitableCustomers(years: Int?): List<TopTrends> =
        dashboardDataRepository.getMostProfitableCustomers(years).map { it.toModel() }

    override fun getOverdueInvoices(years: Int?) =
        dashboardDataRepository.getOverdueInvoices(years).map { it.toModel() }

    override fun getNewCustomersByMonth(years: Int?): List<DashboardMonthlyCount> =
        dashboardDataRepository.getNewCustomersByMonth(years).map { it.toModel() }
}