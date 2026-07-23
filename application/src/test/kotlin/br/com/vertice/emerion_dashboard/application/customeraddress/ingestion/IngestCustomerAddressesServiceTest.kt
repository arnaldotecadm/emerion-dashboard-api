package br.com.vertice.emerion_dashboard.application.customeraddress.ingestion

import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressDetailCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestOutcome
import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddress
import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddressDetail
import br.com.vertice.emerion_dashboard.domain.customeraddress.repository.CustomerAddressRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals

class IngestCustomerAddressesServiceTest {

    private val fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private val customerAddressRepository = mockk<CustomerAddressRepository>()
    private val service = IngestCustomerAddressesService(customerAddressRepository, fixedClock)

    private fun detailCommand(tipo: String) = IngestCustomerAddressDetailCommand(
        tipo = tipo,
        cep = "12345-678",
        endereco = "Rua Teste",
        numero = "100",
        referencia = null,
        bairro = "Centro",
        cidade = "Sao Paulo",
        uf = "SP",
        telefone = "1122223333",
        telefoneContato = null,
        complemento = null,
        fax = null,
    )

    private fun detailDomain(tipo: String) = CustomerAddressDetail(
        tipo = tipo,
        cep = "12345-678",
        endereco = "Rua Teste",
        numero = "100",
        referencia = null,
        bairro = "Centro",
        cidade = "Sao Paulo",
        uf = "SP",
        telefone = "1122223333",
        telefoneContato = null,
        complemento = null,
        fax = null,
    )

    @Test
    fun `creates a new customer address set when externalId is not known yet`() {
        every { customerAddressRepository.findByExternalId("100") } returns null
        val savedSlot = slot<CustomerAddress>()
        every { customerAddressRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-1",
                items = listOf(
                    IngestCustomerAddressCommand(
                        externalId = "100",
                        cnpjEmpresa = "12345678000190",
                        cpfCnpj = null,
                        enderecos = listOf(detailCommand("FATURAMENTO")),
                    ),
                ),
            ),
        )

        assertEquals(1, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(0, result.totalFailed)
        assertEquals(IngestOutcome.CREATED, result.results.single().outcome)
        assertEquals("100", savedSlot.captured.externalId)
        assertEquals(1, savedSlot.captured.enderecos.size)
        verify(exactly = 1) { customerAddressRepository.save(any()) }
    }

    @Test
    fun `updates an existing customer address set when externalId is already known (idempotent re-run)`() {
        val existing = CustomerAddress(
            id = 42L,
            externalId = "200",
            cnpjEmpresa = "12345678000199",
            cpfCnpj = "12345678900",
            enderecos = listOf(detailDomain("FATURAMENTO")),
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z"),
        )
        every { customerAddressRepository.findByExternalId("200") } returns existing
        every { customerAddressRepository.save(any()) } answers { firstArg() }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-2",
                items = listOf(
                    IngestCustomerAddressCommand(
                        externalId = "200",
                        cnpjEmpresa = "99999999000199",
                        cpfCnpj = null,
                        enderecos = listOf(detailCommand("FATURAMENTO"), detailCommand("ENTREGA")),
                    ),
                ),
            ),
        )

        assertEquals(IngestOutcome.UPDATED, result.results.single().outcome)
        verify(exactly = 1) {
            customerAddressRepository.save(match { it.id == 42L && it.enderecos.size == 2 && it.cnpjEmpresa == "99999999000199" })
        }
    }

    @Test
    fun `records a failure for one item without aborting the rest of the batch`() {
        every { customerAddressRepository.findByExternalId("OK") } returns null
        every { customerAddressRepository.findByExternalId("BAD") } throws RuntimeException("db down")
        every { customerAddressRepository.save(any()) } answers { firstArg<CustomerAddress>().copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-3",
                items = listOf(
                    IngestCustomerAddressCommand("OK", "12345678000199", null, listOf(detailCommand("FATURAMENTO"))),
                    IngestCustomerAddressCommand("BAD", "12345678000199", null, listOf(detailCommand("FATURAMENTO"))),
                ),
            ),
        )

        assertEquals(2, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(1, result.totalFailed)
    }

    @Test
    fun `ingestSingle creates a new customer address set when externalId is not known yet`() {
        every { customerAddressRepository.findByExternalId("300") } returns null
        val savedSlot = slot<CustomerAddress>()
        every { customerAddressRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingestSingle(
            IngestCustomerAddressCommand("300", "12345678000199", null, listOf(detailCommand("FATURAMENTO"))),
        )

        assertEquals(IngestOutcome.CREATED, result.outcome)
        assertEquals("300", result.externalId)
        verify(exactly = 1) { customerAddressRepository.save(any()) }
    }

    @Test
    fun `ingestSingle reports a failure without throwing when the item fails`() {
        every { customerAddressRepository.findByExternalId("400") } throws RuntimeException("db down")

        val result = service.ingestSingle(
            IngestCustomerAddressCommand("400", "12345678000199", null, listOf(detailCommand("FATURAMENTO"))),
        )

        assertEquals(IngestOutcome.FAILED, result.outcome)
        assertEquals("db down", result.errorMessage)
    }
}
