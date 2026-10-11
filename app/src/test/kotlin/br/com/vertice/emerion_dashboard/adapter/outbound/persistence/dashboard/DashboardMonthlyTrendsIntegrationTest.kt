package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.dashboard

import br.com.vertice.emerion_dashboard.application.outbound.port.DashboardRepositoryPort
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.sql.Date
import java.time.LocalDate
import java.time.YearMonth
import javax.sql.DataSource
import kotlin.test.assertEquals

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

        insertCustomerOrder("dashboard-order-current", currentDate)
        insertCustomerOrder("dashboard-order-previous", previousMonthDate)
        insertCustomerQuotation("dashboard-quotation-current", currentDate)
        insertCustomerQuotation("dashboard-quotation-earlier", twoMonthsAgoDate)

        val trends = dashboardRepository.getDashboardMonthlyTrends()

        assertEquals(36, trends.size)
        assertEquals(trends.map { it.yearMonth }.sorted(), trends.map { it.yearMonth })
        assertEquals(1, trends.single { it.yearMonth == currentMonth.toString() }.customerOrders)
        assertEquals(1, trends.single { it.yearMonth == currentMonth.toString() }.customerQuotations)
        assertEquals(1, trends.single { it.yearMonth == currentMonth.minusMonths(1).toString() }.customerOrders)
        assertEquals(0, trends.single { it.yearMonth == currentMonth.minusMonths(1).toString() }.customerQuotations)
        assertEquals(0, trends.single { it.yearMonth == currentMonth.minusMonths(2).toString() }.customerOrders)
        assertEquals(1, trends.single { it.yearMonth == currentMonth.minusMonths(2).toString() }.customerQuotations)
    }

    private fun insertCustomerOrder(externalId: String, dataPedido: LocalDate) {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                insert into emerion_dashboard.customer_order
                    (cnpj_empresa, external_id, codigo_cliente, data_pedido)
                values (?, ?, ?, ?)
                """.trimIndent(),
            ).use { statement ->
                statement.setString(1, "15323240000102")
                statement.setString(2, externalId)
                statement.setInt(3, 3206)
                statement.setDate(4, Date.valueOf(dataPedido))
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
