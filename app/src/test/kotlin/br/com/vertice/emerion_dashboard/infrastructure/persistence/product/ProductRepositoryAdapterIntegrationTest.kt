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
                cnpjEmpresa = "12345678000199",
                nome = "Produto 1",
                preco = BigDecimal("19.9000"),
                unidadeEntrada = "PC",
                unidadeSaida = "PC",
                descontoPadrao = BigDecimal("1.0000"),
                estoqueDisponivel = BigDecimal("2.0000"),
                estoqueMinimo = BigDecimal("3.0000"),
                estoqueMaximo = BigDecimal("4.0000"),
                estoqueReservado = BigDecimal("-5.0000"),
                estoqueAdquirido = BigDecimal("6.0000"),
                estoqueAtual = BigDecimal("7.0000"),
                estoqueRma = BigDecimal("8.0000"),
                similar = "prod-ext-similar",
                quantidadeVolumes = BigDecimal("9.0000"),
                quantidadeEmbalagem = BigDecimal("10.0000"),
                localizacao = "A1-B2",
                cubagem = BigDecimal("11.0000"),
                codigoBarrasEmbalagem = "7899646300017",
                ibsCClassTrib = "123",
                ibsCst = "456",
                fcpEntrada = BigDecimal("12.0000"),
                fcpSaida = BigDecimal("13.0000"),
                ipiSaida = "85444200S2",
                ipiEntrada = "85444200E2",
                icmSaida = "85444200S2",
                icmEntrada = "85444200E2",
                icmStSaida = "85444200S2",
                icmStEntrada = "85444200E2",
                observacao = "AÇO, COBRE, PVC",
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
                cnpjEmpresa = "12345678000199",
                nome = "Outro Produto",
                preco = BigDecimal("5.0000"),
                now = now,
            ),
        )
        productRepository.save(
            Product.newFromIngestion(
                externalId = "prod-ext-match",
                cnpjEmpresa = "12345678000199",
                nome = "Produto Especial",
                preco = BigDecimal("10.0000"),
                now = now,
            ),
        )

        val page = productRepository.findAll(
            pageRequest = PageRequest(page = 0, size = 10),
            nomeContains = "Especial",
            cnpjEmpresa = null,
        )

        assertEquals(listOf("prod-ext-match"), page.content.map { it.externalId })
    }
}
