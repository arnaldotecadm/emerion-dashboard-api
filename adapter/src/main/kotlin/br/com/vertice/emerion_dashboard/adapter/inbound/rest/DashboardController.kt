package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.DashboardMapper.toResponse
import br.com.vertice.emerion_dashboard.application.dashboard.DashboardSummaryService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.DashboardApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardMonthlyCount
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardOrdersByRegion
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.OverdueInvoice
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardTopTrendsResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerRegionDistribution
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardCustomerTopTrendsResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter

@RestController
class DashboardController(
    private val dashboardSummaryService: DashboardSummaryService
) : DashboardApi {
    override fun getDashboardCustomerTopTrends(years: Int?): ResponseEntity<DashboardCustomerTopTrendsResponse> {
        val trends = dashboardSummaryService.getCustomerTopTrends(years)
        return ResponseEntity.ok(
            DashboardCustomerTopTrendsResponse(
                customerDistributionByRegion = trends.customerDistributionByRegion.map {
                    CustomerRegionDistribution(region = it.region, customerCount = it.customerCount)
                },
                activeCustomers = trends.activeCustomers,
                unpaidInvoicesAmount = trends.unpaidInvoicesAmount,
                monthlyAverageTicket = trends.monthlyAverageTicket,
                mostProfitableCustomers = trends.mostProfitableCustomers.map { it.toResponse() },
                newCustomersByMonth = trends.newCustomersByMonth.map {
                    DashboardMonthlyCount(yearMonth = it.yearMonth, count = it.count)
                },
                overdueInvoices = trends.overdueInvoices.map {
                    OverdueInvoice(
                        customerCode = it.customerCode,
                        customerName = it.customerName,
                        amount = it.amount,
                        dueDate = it.dueDate,
                        description = "Vencido há ${it.daysOverdue} days (${it.dueDate.format(OVERDUE_DATE_FORMAT)})",
                    )
                },
            ),
        )
    }

    override fun getDashboardTopTrends(orderby: String, years: Int?): ResponseEntity<DashboardTopTrendsResponse> {
        dashboardSummaryService.getDashboardTopTrends(orderby, years).let { topTrends ->
            val response = DashboardTopTrendsResponse(
                topCustomers = topTrends.topCustomers.map { it.toResponse() },
                topVendedores = topTrends.topVendedores.map { it.toResponse() },
                customerOrders = topTrends.customerOrders.map {
                    DashboardMonthlyCount(yearMonth = it.yearMonth, count = it.count)
                },
                customerQuotations = topTrends.customerQuotations.map {
                    DashboardMonthlyCount(yearMonth = it.yearMonth, count = it.count)
                },
                ordersByRegion = topTrends.ordersByRegion.map {
                    DashboardOrdersByRegion(
                        region = it.region,
                        faturado = it.faturado,
                        cancelado = it.cancelado,
                        outros = it.outros,
                    )
                },
                quantidadePedidos = topTrends.quantidadePedidos,
                totalFaturado = topTrends.totalFaturado,
                quantidadeClientes = topTrends.quantidadeClientes,
                quantidadeVendedores = topTrends.quantidadeVendedores,
                ticketMedio = topTrends.ticketMedio
            )
            return ResponseEntity.ok(response)
        }
    }
}

private val OVERDUE_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
