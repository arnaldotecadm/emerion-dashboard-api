package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder

import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrder
import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrderItem
import br.com.vertice.emerion_dashboard.domain.customerorder.repository.CustomerOrderRepository
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals

/**
 * Exercises `CustomerOrderRepositoryAdapter` against a real Postgres
 * instance, covering both the write path (JPA-entity-backed upsert via
 * `save`) and the read path (header + item native-query projections via
 * `CustomerOrderQueryRepository`).
 */
@SpringBootTest
class CustomerOrderRepositoryAdapterIntegrationTest(
    @Autowired private val customerOrderRepository: CustomerOrderRepository,
) : PostgresIntegrationTest() {

    private fun item(produto: String) = CustomerOrderItem(
        produto = produto,
        descricao = "Descricao $produto",
        quantidade = BigDecimal("2.0000"),
        valorUnitario = BigDecimal("10.0000"),
        valorTotal = BigDecimal("20.0000"),
    )

    @Test
    fun `saves a customer order and reads it back through the native query projections`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val saved = customerOrderRepository.save(
            CustomerOrder.newFromIngestion(
                externalId = "order-ext-1",
                codCli = "cust-1",
                cnpjEmpresa = "00000000000191",
                nronfe = "NF-1",
                dteres = now,
                sitres = "ABERTO",
                totger = BigDecimal("100.0000"),
                totres = BigDecimal("90.0000"),
                totipi = BigDecimal("5.0000"),
                totsub = BigDecimal("95.0000"),
                totdescinc = BigDecimal("0.0000"),
                itens = listOf(item("1.1.1"), item("1.1.2")),
                now = now,
            ),
        )

        val found = customerOrderRepository.findById(saved.id!!)

        assertEquals(saved, found)
    }

    @Test
    fun `searches customer orders with filters through the native query projections`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        customerOrderRepository.save(
            CustomerOrder.newFromIngestion(
                externalId = "order-ext-other",
                codCli = "cust-other",
                cnpjEmpresa = null,
                nronfe = null,
                dteres = now,
                sitres = "FECHADO",
                totger = BigDecimal("10.0000"),
                totres = BigDecimal("10.0000"),
                totipi = BigDecimal("0.0000"),
                totsub = BigDecimal("10.0000"),
                totdescinc = BigDecimal("0.0000"),
                itens = listOf(item("2.1.1")),
                now = now,
            ),
        )
        customerOrderRepository.save(
            CustomerOrder.newFromIngestion(
                externalId = "order-ext-match",
                codCli = "cust-match",
                cnpjEmpresa = null,
                nronfe = null,
                dteres = now,
                sitres = "ABERTO",
                totger = BigDecimal("10.0000"),
                totres = BigDecimal("10.0000"),
                totipi = BigDecimal("0.0000"),
                totsub = BigDecimal("10.0000"),
                totdescinc = BigDecimal("0.0000"),
                itens = listOf(item("3.1.1")),
                now = now,
            ),
        )

        val page = customerOrderRepository.findAll(
            pageRequest = PageRequest(page = 0, size = 10),
            codCli = "cust-match",
            sitres = "ABERTO",
        )

        assertEquals(listOf("order-ext-match"), page.content.map { it.externalId })
    }
}
