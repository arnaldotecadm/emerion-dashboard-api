package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer

import br.com.vertice.emerion_dashboard.domain.customer.model.Customer
import br.com.vertice.emerion_dashboard.domain.customer.repository.CustomerRepository
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import kotlin.test.assertEquals

/**
 * Exercises `CustomerRepositoryAdapter` against a real Postgres instance,
 * covering both the write path (JPA-entity-backed upsert via `save`) and the
 * read path (native-query + projection via `CustomerQueryRepository`).
 */
@SpringBootTest
class CustomerRepositoryAdapterIntegrationTest(
    @Autowired private val customerRepository: CustomerRepository,
) : PostgresIntegrationTest() {

    @Test
    fun `saves a customer and reads it back through the native query projection`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val saved = customerRepository.save(
            Customer.newFromIngestion(
                externalId = "ext-1",
                nomeFantasia = "Fantasia 1",
                razaoSocial = "Razao 1",
                cpfCnpj = "12345678900",
                inscricaoEstadual = "IE-1",
                regimeTributario = "Simples",
                bloqueado = false,
                createdAt = null,
                now = now,
            ),
        )

        val found = customerRepository.findById(saved.id!!)

        assertEquals(saved, found)
    }

    @Test
    fun `searches customers with filters through the native query projection`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        customerRepository.save(
            Customer.newFromIngestion(
                externalId = "ext-blocked",
                nomeFantasia = "Blocked Customer",
                razaoSocial = "Razao Blocked",
                cpfCnpj = "11111111111",
                inscricaoEstadual = null,
                regimeTributario = null,
                bloqueado = true,
                createdAt = null,
                now = now,
            ),
        )
        customerRepository.save(
            Customer.newFromIngestion(
                externalId = "ext-active",
                nomeFantasia = "Active Customer",
                razaoSocial = "Razao Active",
                cpfCnpj = "22222222222",
                inscricaoEstadual = null,
                regimeTributario = null,
                bloqueado = false,
                createdAt = null,
                now = now,
            ),
        )

        val page = customerRepository.findAll(
            pageRequest = PageRequest(page = 0, size = 10),
            bloqueado = false,
            nomeFantasiaContains = "Active",
        )

        assertEquals(listOf("ext-active"), page.content.map { it.externalId })
    }
}

