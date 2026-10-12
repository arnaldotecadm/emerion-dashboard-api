package br.com.vertice.emerion_dashboard.application.dashboard

import br.com.vertice.emerion_dashboard.application.outbound.port.DashboardRepositoryPort
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyTrend
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardOrdersByRegion
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTotais
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
        every { repository.getDashboardSellerTopTrends(any()) } returns emptyList()

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
}
