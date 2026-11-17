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
import java.time.LocalDate

@Component
class DashboardRepositoryPortAdapter(
    private val dashboardDataRepository: DashboardDataRepository,
    private val userService: UserService
) : DashboardRepositoryPort {
    override fun getDashboardCustomerTopTrends(
        orderBy: String,
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): List<TopTrends> {
        return dashboardDataRepository.getDashboardCustomerTopTrends(
            orderBy,
            years,
            startDate,
            endDate,
            companyCnpj(),
        ).map { it.toModel() }
    }

    override fun getDashboardSellerTopTrends(
        orderBy: String,
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): List<TopTrends> {
        return dashboardDataRepository.getDashboardSellerTopTrends(orderBy, years, startDate, endDate, companyCnpj()).map { it.toModel() }
    }

    override fun getDashboardMonthlyTrends(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): List<DashboardMonthlyTrend> =
        dashboardDataRepository.getDashboardMonthlyTrends(years, startDate, endDate, companyCnpj()).map { it.toModel() }

    override fun getDashboardOrdersByRegion(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): List<DashboardOrdersByRegion> =
        dashboardDataRepository.getDashboardOrdersByRegion(
            years,
            startDate,
            endDate,
            companyCnpj(),
        ).map { it.toModel() }

    override fun getDashboardTotais(years: Int?, startDate: LocalDate?, endDate: LocalDate?): DashboardTotais {
            return dashboardDataRepository.getDashboardTotais(years, startDate, endDate, companyCnpj()).toModel()
    }

    override fun getCustomerDistributionByRegion(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): List<CustomerRegionDistribution> =
        dashboardDataRepository.getCustomerDistributionByRegion(years, startDate, endDate, companyCnpj()).map { it.toModel() }

    override fun getActiveCustomerCount(
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): Int = dashboardDataRepository.getActiveCustomerCount(years, startDate, endDate, companyCnpj())

    override fun getUnpaidInvoicesAmount(years: Int?, startDate: LocalDate?, endDate: LocalDate?) =
        dashboardDataRepository.getUnpaidInvoicesAmount(years, startDate, endDate, companyCnpj())

    override fun getCustomerMonthlyAverageTicket(years: Int?, startDate: LocalDate?, endDate: LocalDate?): BigDecimal =
        dashboardDataRepository.getCustomerMonthlyAverageTicket(years, startDate, endDate, companyCnpj())

    override fun getMostProfitableCustomers(years: Int?, startDate: LocalDate?, endDate: LocalDate?): List<TopTrends> =
        dashboardDataRepository.getMostProfitableCustomers(years, startDate, endDate, companyCnpj()).map { it.toModel() }

    override fun getOverdueInvoices(years: Int?, startDate: LocalDate?, endDate: LocalDate?) =
        dashboardDataRepository.getOverdueInvoices(years, startDate, endDate, companyCnpj()).map { it.toModel() }

    override fun getCockpitMonthlyMetrics() = dashboardDataRepository.getCockpitMonthlyMetrics(companyCnpj()).toModel()

    override fun getUnpaidInvoicesSummary() = dashboardDataRepository.getUnpaidInvoicesSummary(companyCnpj()).toModel()

    override fun getRecentOrders(limit: Int) = dashboardDataRepository.getRecentOrders(limit, companyCnpj()).map { it.toModel() }

    override fun getNewCustomersByMonth(years: Int?, startDate: LocalDate?, endDate: LocalDate?): List<DashboardMonthlyCount> =
        dashboardDataRepository.getNewCustomersByMonth(years, startDate, endDate, companyCnpj()).map { it.toModel() }

    private fun companyCnpj(): String = userService.getCurrentCompanyCnpj()
}