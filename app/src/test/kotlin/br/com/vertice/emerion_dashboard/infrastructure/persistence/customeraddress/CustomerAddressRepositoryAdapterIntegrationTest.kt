package br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress

import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddress
import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddressDetail
import br.com.vertice.emerion_dashboard.domain.customeraddress.repository.CustomerAddressRepository
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import kotlin.test.assertEquals

/**
 * Exercises `CustomerAddressRepositoryAdapter` against a real Postgres
 * instance, covering both the write path (JPA-entity-backed upsert via
 * `save`) and the read path (header + detail native-query projections via
 * `CustomerAddressQueryRepository`).
 */
@SpringBootTest
class CustomerAddressRepositoryAdapterIntegrationTest(
    @Autowired private val customerAddressRepository: CustomerAddressRepository,
) : PostgresIntegrationTest() {

    private fun detail(tipo: String) = CustomerAddressDetail(
        tipo = tipo,
        cep = "12345-000",
        endereco = "Rua Teste",
        numero = "100",
        referencia = null,
        bairro = "Centro",
        cidade = "Sao Paulo",
        uf = "SP",
        telefone = null,
        telefoneContato = null,
        complemento = null,
        fax = null,
    )

    @Test
    fun `saves a customer address set and reads it back through the native query projections`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val saved = customerAddressRepository.save(
            CustomerAddress.newFromIngestion(
                externalId = "addr-ext-1",
                cnpjEmpresa = "00000000000191",
                cpfCnpj = "12345678900",
                enderecos = listOf(detail("FATURAMENTO"), detail("ENTREGA")),
                now = now,
            ),
        )

        val found = customerAddressRepository.findById(saved.id!!)

        // The detail read query orders rows by `tipo` for deterministic
        // pagination/output, so compare address details independent of the
        // insertion order.
        assertEquals(saved.copy(enderecos = emptyList()), found?.copy(enderecos = emptyList()))
        assertEquals(saved.enderecos.sortedBy { it.tipo }, found?.enderecos)
    }

    @Test
    fun `searches customer address sets with filters through the native query projections`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        customerAddressRepository.save(
            CustomerAddress.newFromIngestion(
                externalId = "addr-ext-other",
                cnpjEmpresa = "12345678000199",
                cpfCnpj = "11111111111",
                enderecos = listOf(detail("FATURAMENTO")),
                now = now,
            ),
        )
        customerAddressRepository.save(
            CustomerAddress.newFromIngestion(
                externalId = "addr-ext-match",
                cnpjEmpresa = "99999999000199",
                cpfCnpj = "99999999999",
                enderecos = listOf(detail("COBRANCA")),
                now = now,
            ),
        )

        val page = customerAddressRepository.findAll(
            pageRequest = PageRequest(page = 0, size = 10),
            cpfCnpjContains = "9999999",
            cnpjEmpresa = "99999999000199",
        )

        assertEquals(listOf("addr-ext-match"), page.content.map { it.externalId })
    }
}
