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
import java.time.LocalDate
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

    private fun item(produto: String, seqRe2: Int) = CustomerOrderItem(
        produto = produto,
        descricao = "Descricao $produto",
        quantidade = BigDecimal("2.0000"),
        valorUnitario = BigDecimal("10.0000"),
        valorTotal = BigDecimal("20.0000"),
        seqRe2 = seqRe2,
    )

    @Test
    fun `saves a customer order and reads it back through the native query projections`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val saved = customerOrderRepository.save(
            CustomerOrder.newFromIngestion(
                externalId = "order-ext-1",
                codCli = "cust-1",
                cnpjEmpresa = "00000000000191",
                cpfCnpj = null,
                nronfe = "NF-1",
                dteres = LocalDate.parse("2024-01-01"),
                sitres = "ABERTO",
                totger = BigDecimal("100.0000"),
                totres = BigDecimal("90.0000"),
                totipi = BigDecimal("5.0000"),
                totsub = BigDecimal("95.0000"),
                totdescinc = BigDecimal("0.0000"),
                itens = listOf(item("1.1.1", 1), item("1.1.2", 2)),
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
                cnpjEmpresa = "12345678000199",
                cpfCnpj = null,
                nronfe = null,
                dteres = LocalDate.parse("2024-01-01"),
                sitres = "FECHADO",
                totger = BigDecimal("10.0000"),
                totres = BigDecimal("10.0000"),
                totipi = BigDecimal("0.0000"),
                totsub = BigDecimal("10.0000"),
                totdescinc = BigDecimal("0.0000"),
                itens = listOf(item("2.1.1", 1)),
                now = now,
            ),
        )
        customerOrderRepository.save(
            CustomerOrder.newFromIngestion(
                externalId = "order-ext-match",
                codCli = "cust-match",
                cnpjEmpresa = "99999999000199",
                cpfCnpj = null,
                nronfe = null,
                dteres = LocalDate.parse("2024-01-01"),
                sitres = "ABERTO",
                totger = BigDecimal("10.0000"),
                totres = BigDecimal("10.0000"),
                totipi = BigDecimal("0.0000"),
                totsub = BigDecimal("10.0000"),
                totdescinc = BigDecimal("0.0000"),
                itens = listOf(item("3.1.1", 1)),
                now = now,
            ),
        )

        val page = customerOrderRepository.findAll(
            pageRequest = PageRequest(page = 0, size = 10),
            codCli = "cust-match",
            sitres = "ABERTO",
            cnpjEmpresa = "99999999000199",
        )

        assertEquals(listOf("order-ext-match"), page.content.map { it.externalId })
    }

    @Test
    fun `re-ingesting the same order upserts its items by seqRe2 instead of failing`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val later = Instant.parse("2024-02-01T00:00:00Z")
        val firstSave = customerOrderRepository.save(
            CustomerOrder.newFromIngestion(
                externalId = "order-ext-reingest",
                codCli = "cust-reingest",
                cnpjEmpresa = "12345678000199",
                cpfCnpj = null,
                nronfe = null,
                dteres = LocalDate.parse("2024-01-01"),
                sitres = "ABERTO",
                totger = BigDecimal("10.0000"),
                totres = BigDecimal("10.0000"),
                totipi = BigDecimal("0.0000"),
                totsub = BigDecimal("10.0000"),
                totdescinc = BigDecimal("0.0000"),
                itens = listOf(item("4.1.1", 1), item("4.1.2", 2)),
                now = now,
            ),
        )

        val existing = customerOrderRepository.findByExternalId("order-ext-reingest")!!
        val resaved = customerOrderRepository.save(
            existing.mergeFromIngestion(
                codCli = existing.codCli,
                cnpjEmpresa = existing.cnpjEmpresa,
                cpfCnpj = null,
                nronfe = existing.nronfe,
                dteres = existing.dteres,
                sitres = "FATURADO",
                totger = existing.totger,
                totres = existing.totres,
                totipi = existing.totipi,
                totsub = existing.totsub,
                totdescinc = existing.totdescinc,
                // Same seqRe2 keys as before (upserted), same produto repeated
                // across two lines (only distinguishable by seqRe2), plus one
                // brand-new line.
                itens = listOf(
                    item("4.1.1", 1).copy(descricao = "Updated"),
                    item("4.1.1", 2),
                    item("4.1.3", 3),
                ),
                now = later,
            ),
        )

        val found = customerOrderRepository.findById(resaved.id!!)!!
        assertEquals(firstSave.id, resaved.id)
        assertEquals("FATURADO", found.sitres)
        assertEquals(3, found.itens.size)
        assertEquals(setOf(1, 2, 3), found.itens.map { it.seqRe2 }.toSet())
        assertEquals("Updated", found.itens.single { it.seqRe2 == 1 }.descricao)
    }
}
