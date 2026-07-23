package br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor

import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.domain.vendedor.model.Vendedor
import br.com.vertice.emerion_dashboard.domain.vendedor.repository.VendedorRepository
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import kotlin.test.assertEquals

/**
 * Exercises `VendedorRepositoryAdapter` against a real Postgres instance,
 * covering both the write path (JPA-entity-backed upsert via `save`) and the
 * read path (native-query + projection via `VendedorQueryRepository`).
 */
@SpringBootTest
class VendedorRepositoryAdapterIntegrationTest(
    @Autowired private val vendedorRepository: VendedorRepository,
) : PostgresIntegrationTest() {

    @Test
    fun `saves a vendedor and reads it back through the native query projection`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val saved = vendedorRepository.save(
            Vendedor.newFromIngestion(
                externalId = "vend-ext-1",
                cnpjEmpresa = "12345678000199",
                nome = "Fulano de Tal",
                apelido = "Fulano",
                cpfCnpj = "12345678900",
                telefone = "1111-1111",
                celular = "99999-9999",
                email = "fulano@example.com",
                cidade = "Sao Paulo",
                uf = "SP",
                situacao = "ATIVO",
                saldo = BigDecimal("1000.0000"),
                dataCadastro = LocalDate.parse("2023-06-01"),
                now = now,
            ),
        )

        val found = vendedorRepository.findById(saved.id!!)

        assertEquals(saved, found)
    }

    @Test
    fun `searches vendedores with filters through the native query projection`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        vendedorRepository.save(
            Vendedor.newFromIngestion(
                externalId = "vend-ext-other",
                cnpjEmpresa = "12345678000199",
                nome = "Outro Vendedor",
                apelido = null,
                cpfCnpj = null,
                telefone = null,
                celular = null,
                email = null,
                cidade = null,
                uf = null,
                situacao = "INATIVO",
                saldo = BigDecimal("0.0000"),
                dataCadastro = null,
                now = now,
            ),
        )
        vendedorRepository.save(
            Vendedor.newFromIngestion(
                externalId = "vend-ext-match",
                cnpjEmpresa = "12345678000199",
                nome = "Vendedor Especial",
                apelido = null,
                cpfCnpj = null,
                telefone = null,
                celular = null,
                email = null,
                cidade = null,
                uf = null,
                situacao = "ATIVO",
                saldo = BigDecimal("500.0000"),
                dataCadastro = null,
                now = now,
            ),
        )

        val page = vendedorRepository.findAll(
            pageRequest = PageRequest(page = 0, size = 10),
            nomeContains = "Especial",
            situacao = "ATIVO",
            cnpjEmpresa = null,
        )

        assertEquals(listOf("vend-ext-match"), page.content.map { it.externalId })
    }
}
