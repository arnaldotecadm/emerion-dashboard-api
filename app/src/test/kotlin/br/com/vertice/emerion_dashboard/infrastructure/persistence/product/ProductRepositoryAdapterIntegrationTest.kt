package br.com.vertice.emerion_dashboard.infrastructure.persistence.product

import br.com.vertice.emerion_dashboard.domain.product.model.Product
import br.com.vertice.emerion_dashboard.domain.product.repository.ProductRepository
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals

/**
 * Exercises `ProductRepositoryAdapter` against a real Postgres instance,
 * covering both the write path (JPA-entity-backed upsert via `save`) and the
 * read path (native-query + projection via `ProductQueryRepository`).
 */
@SpringBootTest
class ProductRepositoryAdapterIntegrationTest(
    @Autowired private val productRepository: ProductRepository,
) : PostgresIntegrationTest() {

    @Test
    fun `saves a product and reads it back through the native query projection`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val saved = productRepository.save(
            Product.newFromIngestion(
                externalId = "prod-ext-1",
                nome = "Produto 1",
                preco = BigDecimal("19.9000"),
                now = now,
            ),
        )

        val found = productRepository.findById(saved.id!!)

        assertEquals(saved, found)
    }

    @Test
    fun `searches products with filters through the native query projection`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        productRepository.save(
            Product.newFromIngestion(
                externalId = "prod-ext-other",
                nome = "Outro Produto",
                preco = BigDecimal("5.0000"),
                now = now,
            ),
        )
        productRepository.save(
            Product.newFromIngestion(
                externalId = "prod-ext-match",
                nome = "Produto Especial",
                preco = BigDecimal("10.0000"),
                now = now,
            ),
        )

        val page = productRepository.findAll(
            pageRequest = PageRequest(page = 0, size = 10),
            nomeContains = "Especial",
        )

        assertEquals(listOf("prod-ext-match"), page.content.map { it.externalId })
    }
}
