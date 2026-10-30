package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.dashboard

import br.com.vertice.emerion_dashboard.application.outbound.port.DashboardRepositoryPort
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Date
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID
import javax.sql.DataSource
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
class CustomerTopTrendsIntegrationTest(
    @Autowired private val dashboardRepository: DashboardRepositoryPort,
    @Autowired private val dataSource: DataSource,
) : PostgresIntegrationTest() {

    @Test
    fun `returns customer region status ticket profit and registration trends`() {
        val currentMonth = YearMonth.now()
        val previousMonth = currentMonth.minusMonths(1)
        val allTimeBaseline = orderTotalsForAllTime()
        val currentCalendarYearBaseline = orderTotalsForCalendarYear(YearMonth.now().year)
        val initialRegionCount = dashboardRepository.getCustomerDistributionByRegion()
            .singleOrNull { it.region == "Sudeste" }?.customerCount ?: 0
        val initialActiveCount = dashboardRepository.getActiveCustomerCount()
        val initialNewCustomers = dashboardRepository.getNewCustomersByMonth().associate { it.yearMonth to it.count }

        val firstCode = (System.nanoTime() % 800_000_000L).toInt() + 1_000_000_000
        val activeCustomerId = insertCustomer(
            code = firstCode,
            blocked = false,
            region = "SP",
            registeredAt = currentMonth.atDay(10),
        )
        insertCustomer(
            code = firstCode + 1,
            blocked = true,
            region = "SP",
            registeredAt = previousMonth.atDay(10),
        )

        val activeOrderId = insertCustomerOrder(firstCode, "120.00")
        val blockedOrderId = insertCustomerOrder(firstCode + 1, "45.00")
        insertOrderItem(activeOrderId, 900_000_000_000L)
        insertOrderItem(blockedOrderId, 100L)

        val previousYear = YearMonth.now().minusYears(1)
        val previousYearCustomerCode = firstCode + 2
        val previousYearCustomerId = insertCustomer(
            code = previousYearCustomerCode,
            blocked = true,
            region = "RS",
            registeredAt = previousYear.atDay(10),
        )
        val previousYearOrderId = insertCustomerOrder(
            previousYearCustomerCode,
            "100.00",
            previousYear.atDay(10),
        )
        insertOrderItem(previousYearOrderId, 800_000_000_000_000L)

        val distribution = dashboardRepository.getCustomerDistributionByRegion().single { it.region == "Sudeste" }
        assertEquals(initialRegionCount + 2, distribution.customerCount)
        assertEquals(initialActiveCount + 1, dashboardRepository.getActiveCustomerCount())

        val ticketAddedOrders = BigDecimal("165.00")
        val expectedTicket = (allTimeBaseline.totalBilled + ticketAddedOrders + BigDecimal("100.00"))
            .divide((allTimeBaseline.orderCount + 3).toBigDecimal(), 2, RoundingMode.HALF_UP)
        assertEquals(expectedTicket, dashboardRepository.getCustomerMonthlyAverageTicket())
        val expectedCalendarYearTicket = (currentCalendarYearBaseline.totalBilled + ticketAddedOrders)
            .divide((currentCalendarYearBaseline.orderCount + 2).toBigDecimal(), 2, RoundingMode.HALF_UP)
        assertEquals(expectedCalendarYearTicket, dashboardRepository.getCustomerMonthlyAverageTicket(years = 1))

        val mostProfitable = dashboardRepository.getMostProfitableCustomers(years = 1).first()
        assertEquals(activeCustomerId.toInt(), mostProfitable.identifier)
        assertEquals(BigDecimal("900000000000.0000"), mostProfitable.totalSales)
        assertEquals(1, mostProfitable.amountSales)
        assertTrue(
            dashboardRepository.getMostProfitableCustomers(years = 2)
                .any { it.identifier == previousYearCustomerId.toInt() },
        )
        assertFalse(
            dashboardRepository.getMostProfitableCustomers(years = 1)
                .any { it.identifier == previousYearCustomerId.toInt() },
        )

        val newCustomers = dashboardRepository.getNewCustomersByMonth().associate { it.yearMonth to it.count }
        val orderedNewCustomers = dashboardRepository.getNewCustomersByMonth()
        assertEquals(
            orderedNewCustomers.map { it.yearMonth }.sortedDescending(),
            orderedNewCustomers.map { it.yearMonth },
        )
        assertTrue(newCustomers.size >= 13)
        assertEquals((initialNewCustomers[currentMonth.toString()] ?: 0) + 1, newCustomers[currentMonth.toString()])
        assertEquals((initialNewCustomers[previousMonth.toString()] ?: 0) + 1, newCustomers[previousMonth.toString()])
        assertEquals(YearMonth.now().monthValue, dashboardRepository.getNewCustomersByMonth(years = 1).size)
        assertEquals(
            YearMonth.now().monthValue + 12,
            dashboardRepository.getNewCustomersByMonth(years = 2).size,
        )
    }

    @Test
    fun `sums unpaid installments already due`() {
        val initial = dashboardRepository.getUnpaidInvoicesAmount()
        val initialCurrentYear = dashboardRepository.getUnpaidInvoicesAmount(years = 1)
        val document = "trend-doc-${UUID.randomUUID()}"
        dataSource.connection.use { connection ->
            connection.createStatement().use {
                it.execute(
                    "insert into emerion_dashboard.titulo_receber (cnpj_empresa, documento) " +
                        "values ('15323240000102', '$document')",
                )
                it.execute(
                    """
                    insert into emerion_dashboard.titulo_receber_parcela
                        (documento, numero_parcela, valor_parcela, data_vencimento, flag_pago) values
                        ('$document', 1, 100.00, current_date - 5, null),
                        ('$document', 2, 50.00, current_date - 1, ''),
                        ('$document', 3, 70.00, current_date - 5, '*'),
                        ('$document', 4, 30.00, current_date + 30, null),
                        ('$document', 5, 20.00, make_date(extract(year from current_date)::int - 1, 6, 1), null)
                    """.trimIndent(),
                )
            }
        }

        assertEquals(0, (initial + BigDecimal("170.00")).compareTo(dashboardRepository.getUnpaidInvoicesAmount()))
        val overdue = dashboardRepository.getOverdueInvoices()
        assertTrue(overdue.size <= 5)
        assertEquals(overdue.map { it.dueDate }.sorted(), overdue.map { it.dueDate })
        assertTrue(overdue.all { it.daysOverdue >= 0 })
        assertEquals(
            0,
            (initialCurrentYear + BigDecimal("150.00")).compareTo(dashboardRepository.getUnpaidInvoicesAmount(years = 1)),
        )
    }

    @Test
    fun `returns cockpit metrics and recent orders preferring non invoiced`() {
        val code = (System.nanoTime() % 800_000_000L).toInt() + 1_000_000_000
        val statuses = List(12) { "Faturado" } + listOf("Aberto", "Aprovado")
        statuses.forEach { status ->
            val orderId = insertCustomerOrder(code, "10.00", LocalDate.now())
            dataSource.connection.use { connection ->
                connection.prepareStatement(
                    "update emerion_dashboard.customer_order set status_pedido = ? where id = ?",
                ).use { statement ->
                    statement.setString(1, status)
                    statement.setLong(2, orderId)
                    statement.executeUpdate()
                }
            }
            insertOrderItem(orderId, 1)
        }

        val baseMetrics = dashboardRepository.getCockpitMonthlyMetrics()
        insertMonthInstallments()
        val metrics = dashboardRepository.getCockpitMonthlyMetrics()
        assertEquals(0, (baseMetrics.amountToReceive + BigDecimal("300.00")).compareTo(metrics.amountToReceive))
        assertEquals(0, (baseMetrics.amountReceived + BigDecimal("200.00")).compareTo(metrics.amountReceived))
        assertTrue(metrics.customerOrders >= statuses.size)
        assertTrue(metrics.salesTotal >= BigDecimal("140.00"))
        assertTrue(metrics.averageMarkup.signum() >= 0)

        val summary = dashboardRepository.getUnpaidInvoicesSummary()
        assertTrue(summary.count >= 0 && summary.amount.signum() >= 0)
        assertTrue(summary.amount <= dashboardRepository.getUnpaidInvoicesAmount())

        val recent = dashboardRepository.getRecentOrders(10)
        assertEquals(10, recent.size)
        assertTrue(recent.all { it.averageMarkup.signum() >= 0 })
        assertEquals(recent.map { it.orderDate }.sortedDescending(), recent.map { it.orderDate })
        val nonInvoicedTotal = dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(
                    "select count(*) from emerion_dashboard.customer_order " +
                        "where coalesce(status_pedido, '') != 'Faturado'",
                ).use { resultSet ->
                    resultSet.next()
                    resultSet.getInt(1)
                }
            }
        }
        assertEquals(minOf(10, nonInvoicedTotal), recent.count { it.status != "Faturado" })
    }

    private fun insertMonthInstallments() {
        val document = "cockpit-doc-${UUID.randomUUID()}"
        dataSource.connection.use { connection ->
            connection.createStatement().use {
                it.execute(
                    "insert into emerion_dashboard.titulo_receber (cnpj_empresa, documento) " +
                        "values ('15323240000102', '$document')",
                )
                it.execute(
                    """
                    insert into emerion_dashboard.titulo_receber_parcela
                        (documento, numero_parcela, valor_parcela, data_vencimento, flag_pago) values
                        ('$document', 1, 200.00, date_trunc('month', current_date)::date, '*'),
                        ('$document', 2, 100.00, (date_trunc('month', current_date) + interval '1 month')::date - 1, null),
                        ('$document', 3, 999.00, (date_trunc('month', current_date) + interval '1 month')::date, null)
                    """.trimIndent(),
                )
            }
        }
    }

    private fun orderTotalsForAllTime(): MonthOrderTotals =
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                select count(*), coalesce(sum(total_pedido_com_impostos), 0)
                from emerion_dashboard.customer_order
                where data_pedido >= date '1900-01-01'
                  and data_pedido <= current_date
                """.trimIndent(),
            ).use { statement ->
                statement.executeQuery().use { resultSet ->
                    resultSet.next()
                    MonthOrderTotals(resultSet.getInt(1), resultSet.getBigDecimal(2))
                }
            }
        }

    private fun orderTotalsForCalendarYear(year: Int): MonthOrderTotals =
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                select count(*), coalesce(sum(total_pedido_com_impostos), 0)
                from emerion_dashboard.customer_order
                where data_pedido >= make_date(?, 1, 1)
                  and data_pedido < make_date(? + 1, 1, 1)
                  and data_pedido <= current_date
                """.trimIndent(),
            ).use { statement ->
                statement.setInt(1, year)
                statement.setInt(2, year)
                statement.executeQuery().use { resultSet ->
                    resultSet.next()
                    MonthOrderTotals(resultSet.getInt(1), resultSet.getBigDecimal(2))
                }
            }
        }

    private fun insertCustomer(code: Int, blocked: Boolean, region: String, registeredAt: LocalDate): Long =
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                insert into emerion_dashboard.customer (
                    external_id, nome_fantasia, razao_social, cpf_cnpj, cnpj_empresa,
                    bloqueado, uf, data_cadastro
                ) values (?, ?, ?, ?, ?, ?, ?, ?)
                returning id
                """.trimIndent(),
            ).use { statement ->
                statement.setString(1, code.toString())
                statement.setString(2, "Trend $code")
                statement.setString(3, "Trend Customer $code")
                statement.setString(4, code.toString().padStart(11, '0'))
                statement.setString(5, "15323240000102")
                statement.setBoolean(6, blocked)
                statement.setString(7, region)
                statement.setDate(8, Date.valueOf(registeredAt))
                statement.executeQuery().use { resultSet ->
                    resultSet.next()
                    resultSet.getLong(1)
                }
            }
        }

    private fun insertCustomerOrder(
        customerCode: Int,
        total: String,
        dataPedido: LocalDate = LocalDate.now(),
    ): Long =
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                insert into emerion_dashboard.customer_order (
                    cnpj_empresa, external_id, codigo_cliente, data_pedido, total_pedido_com_impostos
                ) values (?, ?, ?, ?, ?)
                returning id
                """.trimIndent(),
            ).use { statement ->
                statement.setString(1, "15323240000102")
                statement.setString(2, "customer-trend-${UUID.randomUUID()}")
                statement.setInt(3, customerCode)
                statement.setDate(4, Date.valueOf(dataPedido))
                statement.setBigDecimal(5, BigDecimal(total))
                statement.executeQuery().use { resultSet ->
                    resultSet.next()
                    resultSet.getLong(1)
                }
            }
        }

    private fun insertOrderItem(customerOrderId: Long, profit: Long) {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                """
                insert into emerion_dashboard.customer_order_item (
                    customer_order_id, produto, seq_re2, lucro_valor
                ) values (?, ?, ?, ?)
                """.trimIndent(),
            ).use { statement ->
                statement.setLong(1, customerOrderId)
                statement.setString(2, "customer-trend-product")
                statement.setInt(3, 1)
                statement.setBigDecimal(4, BigDecimal.valueOf(profit))
                statement.executeUpdate()
            }
        }
    }

    private data class MonthOrderTotals(val orderCount: Int, val totalBilled: BigDecimal)
}
