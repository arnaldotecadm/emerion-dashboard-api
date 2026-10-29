package br.com.vertice.emerion_dashboard.application.dashboard

import br.com.vertice.emerion_dashboard.application.outbound.port.DashboardRepositoryPort
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyTrend
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardOrdersByRegion
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTotais
import br.com.vertice.emerion_dashboard.domain.dashboard.CustomerRegionDistribution
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyCount
import br.com.vertice.emerion_dashboard.domain.dashboard.TopTrends
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class DashboardSummaryServiceTest {
    private val repository = mockk<DashboardRepositoryPort>()
    private val service = DashboardSummaryService(repository)

    @Test
    fun `returns aligned customer order and quotation counts for every month`() {
        every { repository.getDashboardTotais(any()) } returns DashboardTotais(
            quantidadePedidos = 0,
            quantidadeClientes = 0,
            quantidadeVendedores = 0,
            totalFaturado = BigDecimal.ZERO,
        )
        every { repository.getDashboardMonthlyTrends(any()) } returns listOf(
            DashboardMonthlyTrend(
                "2026-01",
                customerOrders = 3,
                customerOrdersAmountSold = BigDecimal("125.50"),
                customerQuotations = 0,
            ),
            DashboardMonthlyTrend(
                "2026-02",
                customerOrders = 0,
                customerOrdersAmountSold = BigDecimal.ZERO,
                customerQuotations = 2,
            ),
        )
        every { repository.getDashboardOrdersByRegion() } returns listOf(
            DashboardOrdersByRegion(
                region = "Sudeste",
                faturado = BigDecimal("100.00"),
                cancelado = BigDecimal("25.00"),
                outros = BigDecimal("10.00"),
            ),
        )
        every { repository.getDashboardCustomerTopTrends(any()) } returns emptyList()
        every { repository.getDashboardSellerTopTrends(any(), any()) } returns emptyList()

        val result = service.getDashboardTopTrends("QUANTIDADEPEDIDO", null)

        assertEquals(
            listOf(
                "2026-01" to 3,
                "2026-02" to 0,
            ),
            result.customerOrders.map { it.yearMonth to it.count },
        )
        assertEquals(
            listOf(
                "2026-01" to 0,
                "2026-02" to 2,
            ),
            result.customerQuotations.map { it.yearMonth to it.count },
        )
        assertEquals("Sudeste", result.ordersByRegion.single().region)
        assertEquals(BigDecimal("100.00"), result.ordersByRegion.single().faturado)
        assertEquals(BigDecimal("25.00"), result.ordersByRegion.single().cancelado)
        assertEquals(BigDecimal("10.00"), result.ordersByRegion.single().outros)
    }

    @Test
    fun `returns monthly order counts and sales amounts for reuse`() {
        every { repository.getDashboardMonthlyTrends(any()) } returns listOf(
            DashboardMonthlyTrend(
                "2026-01",
                customerOrders = 3,
                customerOrdersAmountSold = BigDecimal("125.50"),
                customerQuotations = 0,
            ),
            DashboardMonthlyTrend(
                "2026-02",
                customerOrders = 0,
                customerOrdersAmountSold = BigDecimal.ZERO,
                customerQuotations = 2,
            ),
        )

        val result = service.getCustomerOrdersOverTime()

        assertEquals(listOf("2026-01" to 3, "2026-02" to 0), result.map { it.yearMonth to it.orderCount })
        assertEquals(
            listOf(BigDecimal("125.50"), BigDecimal.ZERO),
            result.map { it.amountSold },
        )
    }

    @Test
    fun `returns customer-specific top trends`() {
        every { repository.getCustomerDistributionByRegion() } returns
            listOf(CustomerRegionDistribution(region = "SP", customerCount = 12))
        every { repository.getActiveCustomerCount() } returns 10
        every { repository.getUnpaidInvoicesAmount(any()) } returns BigDecimal("75.00")
        every { repository.getCustomerMonthlyAverageTicket(any()) } returns BigDecimal("42.50")
        every { repository.getMostProfitableCustomers(any()) } returns
            listOf(TopTrends(identifier = 1, name = "Customer", amountSales = 2, totalSales = BigDecimal("100.00")))
        every { repository.getOverdueInvoices(any()) } returns listOf(
            br.com.vertice.emerion_dashboard.domain.dashboard.OverdueInvoice(
                customerCode = 7L,
                customerName = "Customer",
                amount = BigDecimal("10.00"),
                dueDate = java.time.LocalDate.of(2026, 1, 1),
                daysOverdue = 5,
            ),
        )
        every { repository.getNewCustomersByMonth(any()) } returns
            listOf(DashboardMonthlyCount(yearMonth = "2026-01", count = 3))

        val result = service.getCustomerTopTrends(years = 2)

        assertEquals(12, result.customerDistributionByRegion.single().customerCount)
        assertEquals(10, result.activeCustomers)
        assertEquals(BigDecimal("75.00"), result.unpaidInvoicesAmount)
        assertEquals(BigDecimal("42.50"), result.monthlyAverageTicket)
        assertEquals("Customer", result.mostProfitableCustomers.single().name)
        assertEquals(3, result.newCustomersByMonth.single().count)
        assertEquals(5, result.overdueInvoices.single().daysOverdue)
        assertEquals(7L, result.overdueInvoices.single().customerCode)
        io.mockk.verify {
            repository.getCustomerMonthlyAverageTicket(2)
            repository.getUnpaidInvoicesAmount(2)
            repository.getOverdueInvoices(2)
            repository.getMostProfitableCustomers(2)
            repository.getNewCustomersByMonth(2)
        }
    }
}
