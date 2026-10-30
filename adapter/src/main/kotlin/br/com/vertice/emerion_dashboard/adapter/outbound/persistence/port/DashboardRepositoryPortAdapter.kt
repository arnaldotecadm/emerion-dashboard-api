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
        return dashboardDataRepository.getDashboardCustomerTopTrends(orderBy, companyCnpj()).map { it.toModel() }
    }

    override fun getDashboardSellerTopTrends(orderBy: String, years: Int?): List<TopTrends> {
        return dashboardDataRepository.getDashboardSellerTopTrends(orderBy, years, companyCnpj()).map { it.toModel() }
    }

    override fun getDashboardMonthlyTrends(years: Int?): List<DashboardMonthlyTrend> =
        dashboardDataRepository.getDashboardMonthlyTrends(years, companyCnpj()).map { it.toModel() }

    override fun getDashboardOrdersByRegion(): List<DashboardOrdersByRegion> =
        dashboardDataRepository.getDashboardOrdersByRegion(companyCnpj()).map { it.toModel() }

    override fun     getDashboardTotais(years: Int?): DashboardTotais {
            return dashboardDataRepository.getDashboardTotais(years, companyCnpj()).toModel()
    }

    override fun getCustomerDistributionByRegion(): List<CustomerRegionDistribution> =
        dashboardDataRepository.getCustomerDistributionByRegion(companyCnpj()).map { it.toModel() }

    override fun getActiveCustomerCount(): Int = dashboardDataRepository.getActiveCustomerCount(companyCnpj())

    override fun getUnpaidInvoicesAmount(years: Int?) = dashboardDataRepository.getUnpaidInvoicesAmount(years, companyCnpj())

    override fun getCustomerMonthlyAverageTicket(years: Int?): BigDecimal =
        dashboardDataRepository.getCustomerMonthlyAverageTicket(years, companyCnpj())

    override fun getMostProfitableCustomers(years: Int?): List<TopTrends> =
        dashboardDataRepository.getMostProfitableCustomers(years, companyCnpj()).map { it.toModel() }

    override fun getOverdueInvoices(years: Int?) =
        dashboardDataRepository.getOverdueInvoices(years, companyCnpj()).map { it.toModel() }

    override fun getCockpitMonthlyMetrics() = dashboardDataRepository.getCockpitMonthlyMetrics(companyCnpj()).toModel()

    override fun getUnpaidInvoicesSummary() = dashboardDataRepository.getUnpaidInvoicesSummary(companyCnpj()).toModel()

    override fun getRecentOrders(limit: Int) = dashboardDataRepository.getRecentOrders(limit, companyCnpj()).map { it.toModel() }

    override fun getNewCustomersByMonth(years: Int?): List<DashboardMonthlyCount> =
        dashboardDataRepository.getNewCustomersByMonth(years, companyCnpj()).map { it.toModel() }

    private fun companyCnpj(): String = userService.getCurrentCompanyCnpj()
}