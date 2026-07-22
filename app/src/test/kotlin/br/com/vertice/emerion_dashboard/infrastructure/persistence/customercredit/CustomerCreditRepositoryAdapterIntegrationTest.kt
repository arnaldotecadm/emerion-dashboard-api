package br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit

import br.com.vertice.emerion_dashboard.domain.customercredit.model.CustomerCredit
import br.com.vertice.emerion_dashboard.domain.customercredit.repository.CustomerCreditRepository
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals

/**
 * Exercises `CustomerCreditRepositoryAdapter` against a real Postgres
 * instance, covering both the write path (JPA-entity-backed upsert via
 * `save`) and the read path (native-query + projection via
 * `CustomerCreditQueryRepository`).
 */
@SpringBootTest
class CustomerCreditRepositoryAdapterIntegrationTest(
    @Autowired private val customerCreditRepository: CustomerCreditRepository,
) : PostgresIntegrationTest() {

    @Test
    fun `saves a customer credit entry and reads it back through the native query projection`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val saved = customerCreditRepository.save(
            CustomerCredit.newFromIngestion(
                customerExternalId = "cust-ext-1",
                sequencia = "1",
                data = now,
                dataPedido = now,
                valorUtilizado = BigDecimal("10.0000"),
                valorTotal = BigDecimal("100.0000"),
                saldo = BigDecimal("90.0000"),
                situacao = "ATIVO",
                tipo = "ENTRADA",
                now = now,
            ),
        )

        val found = customerCreditRepository.findById(saved.id!!)

        assertEquals(saved, found)
    }

    @Test
    fun `searches customer credit entries with filters through the native query projection`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        customerCreditRepository.save(
            CustomerCredit.newFromIngestion(
                customerExternalId = "cust-ext-other",
                sequencia = "1",
                data = now,
                dataPedido = null,
                valorUtilizado = BigDecimal("5.0000"),
                valorTotal = BigDecimal("50.0000"),
                saldo = BigDecimal("45.0000"),
                situacao = null,
                tipo = "SAIDA",
                now = now,
            ),
        )
        customerCreditRepository.save(
            CustomerCredit.newFromIngestion(
                customerExternalId = "cust-ext-match",
                sequencia = "1",
                data = now,
                dataPedido = null,
                valorUtilizado = BigDecimal("5.0000"),
                valorTotal = BigDecimal("50.0000"),
                saldo = BigDecimal("45.0000"),
                situacao = null,
                tipo = "ENTRADA",
                now = now,
            ),
        )

        val page = customerCreditRepository.findAll(
            pageRequest = PageRequest(page = 0, size = 10),
            customerExternalId = "cust-ext-match",
            tipo = "ENTRADA",
        )

        assertEquals(listOf("cust-ext-match"), page.content.map { it.customerExternalId })
    }
}
