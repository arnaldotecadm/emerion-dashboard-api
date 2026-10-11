package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.dashboard

import br.com.vertice.emerion_dashboard.application.outbound.port.DashboardRepositoryPort
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.sql.Date
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
class DashboardMonthlyTrendsIntegrationTest(
    @Autowired private val dashboardRepository: DashboardRepositoryPort,
    @Autowired private val dataSource: DataSource,
) : PostgresIntegrationTest() {
    @Test
    fun `returns aligned monthly counts for orders and quotations over three years`() {
        val currentMonth = YearMonth.now()
        val currentDate = LocalDate.now()
        val previousMonthDate = currentMonth.minusMonths(1).atDay(1)
        val twoMonthsAgoDate = currentMonth.minusMonths(2).atDay(1)
        val baselineCurrentMonth = dashboardRepository.getDashboardMonthlyTrends()
            .single { it.yearMonth == currentMonth.toString() }

        insertCustomerOrder("dashboard-order-current", currentDate, totalPedido = BigDecimal("10.50"))
        insertCustomerOrder("dashboard-order-previous", previousMonthDate, totalPedido = BigDecimal("7.25"))
        insertCustomerQuotation("dashboard-quotation-current", currentDate)
        insertCustomerQuotation("dashboard-quotation-earlier", twoMonthsAgoDate)

        val trends = dashboardRepository.getDashboardMonthlyTrends()

        assertEquals(36, trends.size)
        assertEquals(trends.map { it.yearMonth }.sorted(), trends.map { it.yearMonth })
        assertEquals(baselineCurrentMonth.customerOrders + 1, trends.single { it.yearMonth == currentMonth.toString() }.customerOrders)
        assertEquals(1, trends.single { it.yearMonth == currentMonth.toString() }.customerQuotations)
        assertEquals(1, trends.single { it.yearMonth == currentMonth.minusMonths(1).toString() }.customerOrders)
        assertEquals(0, trends.single { it.yearMonth == currentMonth.minusMonths(1).toString() }.customerQuotations)
        assertEquals(0, trends.single { it.yearMonth == currentMonth.minusMonths(2).toString() }.customerOrders)
        assertEquals(1, trends.single { it.yearMonth == currentMonth.minusMonths(2).toString() }.customerQuotations)
        assertTrue(
            (trends.single { it.yearMonth == currentMonth.toString() }.customerOrdersAmountSold -
                baselineCurrentMonth.customerOrdersAmountSold).compareTo(BigDecimal("10.50")) == 0,
        )
    }

    @Test
    fun `groups order totals by region and billed cancelled or other status`() {
        val baseline = dashboardRepository.getDashboardOrdersByRegion()
            .singleOrNull { it.region == "Sudeste" }
        val baselineFaturado = baseline?.faturado ?: BigDecimal.ZERO
        val baselineCancelado = baseline?.cancelado ?: BigDecimal.ZERO
        val baselineOutros = baseline?.outros ?: BigDecimal.ZERO
        val baselineMonthlySales = dashboardRepository.getDashboardMonthlyTrends()
            .single { it.yearMonth == YearMonth.now().toString() }.customerOrdersAmountSold

        insertCustomerOrder("dashboard-region-billed", LocalDate.now(), "SP", "Faturado", BigDecimal("100.00"))
        insertCustomerOrder("dashboard-region-cancelled", LocalDate.now(), "SP", "Cancelado", BigDecimal("25.00"))
        insertCustomerOrder("dashboard-region-other", LocalDate.now(), "SP", "Em andamento", BigDecimal("10.00"))

        val sudeste = dashboardRepository.getDashboardOrdersByRegion().single { it.region == "Sudeste" }

        assertTrue((sudeste.faturado - baselineFaturado).compareTo(BigDecimal("100.00")) == 0)
        assertTrue((sudeste.cancelado - baselineCancelado).compareTo(BigDecimal("25.00")) == 0)
        assertTrue((sudeste.outros - baselineOutros).compareTo(BigDecimal("10.00")) == 0)
        val monthlySales = dashboardRepository.getDashboardMonthlyTrends()
            .single { it.yearMonth == YearMonth.now().toString() }
        assertTrue((monthlySales.customerOrdersAmountSold - baselineMonthlySales).compareTo(BigDecimal("135.00")) == 0)
    }

    private fun insertCustomerOrder(
        externalId: String,
        dataPedido: LocalDate,
        ufPedido: String? = null,
        statusPedido: String? = null,
        totalPedido: BigDecimal = BigDecimal.ZERO,
    ) {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                insert into emerion_dashboard.customer_order
                    (cnpj_empresa, external_id, codigo_cliente, data_pedido, uf_pedido, status_pedido, total_pedido_com_impostos)
                values (?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
            ).use { statement ->
                statement.setString(1, "15323240000102")
                statement.setString(2, externalId)
                statement.setInt(3, 3206)
                statement.setDate(4, Date.valueOf(dataPedido))
                statement.setString(5, ufPedido)
                statement.setString(6, statusPedido)
                statement.setBigDecimal(7, totalPedido)
                statement.executeUpdate()
            }
        }
    }

    private fun insertCustomerQuotation(externalId: String, dataPedido: LocalDate) {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                insert into emerion_dashboard.customer_quotation (
                    external_id, cnpj_empresa, codigo_empresa, codigo_cliente, customer_external_id,
                    dteres, numero_pedido, data_pedido, total_pedido_com_impostos,
                    total_pedido_sem_impostos, total_ipi, total_icms, total_pis, total_cofins,
                    total_substituicao_tributaria, total_desconto_incondicional, total_frete,
                    total_seguro, total_outras_despesas
                ) values (?, ?, 1, 3206, 3206, ?, ?, ?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                """.trimIndent(),
            ).use { statement ->
                statement.setString(1, externalId)
                statement.setString(2, "15323240000102")
                statement.setDate(3, Date.valueOf(dataPedido))
                statement.setString(4, externalId)
                statement.setDate(5, Date.valueOf(dataPedido))
                statement.executeUpdate()
            }
        }
    }
}
