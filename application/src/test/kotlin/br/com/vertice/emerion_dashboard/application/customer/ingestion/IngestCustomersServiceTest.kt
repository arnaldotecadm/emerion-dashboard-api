package br.com.vertice.emerion_dashboard.application.customer.ingestion

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestCustomerCommand
import br.com.vertice.emerion_dashboard.application.outbound.port.CustomerRepositoryPort
import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.domain.customeraddress.repository.CustomerAddressRepository
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals

class IngestCustomersServiceTest {

    private val fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private val customerRepository = mockk<CustomerRepositoryPort>()
    private val customerAddressRepository = mockk<CustomerAddressRepository>()
    private val sqsProducer = mockk<SQSProducer>(relaxed = true)
    private val service =
        IngestCustomersService(customerRepository, customerAddressRepository, fixedClock, sqsProducer, "")

    @Test
    fun `creates a new customer when externalId is not known yet`() {
        every { customerRepository.findByExternalId(1L, null) } returns null
        val savedSlot = slot<Customer>()
        every { customerRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-1",
                items = listOf(
                    IngestCustomerCommand(
                        externalId = 1L,
                        cnpjEmpresa = "12345678000199",
                        nomeFantasia = "Acme",
                        razaoSocial = "Acme Corp Ltda",
                        cpfCnpj = "12345678000190",
                        inscricaoEstadual = "123456789",
                        regimeTributario = "SIMPLES_NACIONAL",
                        bloqueado = false,
                        createdAt = null,
                    ),
                ),
            ),
        )

        assertEquals(1, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(0, result.totalFailed)
        assertEquals(IngestOutcome.QUEUED, result.results.single().outcome)
    }

    @Test
    fun `updates an existing customer when externalId is already known (idempotent re-run)`() {
        val existing = Customer(
            id = 42L,
            externalId = 2L,
            cnpjEmpresa = "12345678000199",
            nomeFantasia = "Old Name",
            razaoSocial = "Old Razao Social Ltda",
            cpfCnpj = "12345678000190",
            inscricaoEstadual = null,
            regimeTributario = null,
            bloqueado = true,
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z"),
        )
        every { customerRepository.findByExternalId(2L, null) } returns existing
        every { customerRepository.save(any()) } answers { firstArg() }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-2",
                items = listOf(
                    IngestCustomerCommand(
                        externalId = 2L,
                        cnpjEmpresa = "12345678000199",
                        nomeFantasia = "New Name",
                        razaoSocial = "New Razao Social Ltda",
                        cpfCnpj = "12345678000190",
                        inscricaoEstadual = "987654321",
                        regimeTributario = "LUCRO_PRESUMIDO",
                        bloqueado = false,
                        createdAt = null,
                    ),
                ),
            ),
        )

        assertEquals(IngestOutcome.QUEUED, result.results.single().outcome)
    }

    @Test
    fun `records a failure for one item without aborting the rest of the batch`() {
        every { customerRepository.findByExternalId(3L, null) } returns null
        every { customerRepository.findByExternalId(4L, null) } throws RuntimeException("db down")
        every { customerRepository.save(any()) } answers { firstArg<Customer>().copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-3",
                items = listOf(
                    IngestCustomerCommand(
                        externalId = 3L,
                        cnpjEmpresa = "12345678000199",
                        nomeFantasia = "Ok Co",
                        razaoSocial = "Ok Co Ltda",
                        cpfCnpj = "12345678000190",
                        inscricaoEstadual = null,
                        regimeTributario = null,
                        bloqueado = false,
                        createdAt = null,
                    ),
                    IngestCustomerCommand(
                        externalId = 4L,
                        cnpjEmpresa = "12345678000199",
                        nomeFantasia = "Bad Co",
                        razaoSocial = "Bad Co Ltda",
                        cpfCnpj = "12345678000190",
                        inscricaoEstadual = null,
                        regimeTributario = null,
                        bloqueado = false,
                        createdAt = null,
                    ),
                ),
            ),
        )

        assertEquals(2, result.totalReceived)
    }

    @Test
    fun `ingestSingle creates a new customer when externalId is not known yet`() {
        every { customerRepository.findByExternalId(4L, "12345678000199") } returns null
        val savedSlot = slot<Customer>()
        every { customerRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingestSingle(
            IngestCustomerCommand(
                externalId = 4L,
                cnpjEmpresa = "12345678000199",
                nomeFantasia = "Acme",
                razaoSocial = "Acme Corp Ltda",
                cpfCnpj = "12345678000190",
                inscricaoEstadual = null,
                regimeTributario = null,
                bloqueado = false,
                createdAt = null,
            ),
        )

        assertEquals(IngestOutcome.CREATED, result.outcome)
        assertEquals(4L, result.externalId)
        verify(exactly = 1) { customerRepository.save(any()) }
    }

    @Test
    fun `ingestSingle reports a failure without throwing when the item fails`() {
        every { customerRepository.findByExternalId(5L, "12345678000199") } throws RuntimeException("db down")

        val result = service.ingestSingle(
            IngestCustomerCommand(
                externalId = 5L,
                cnpjEmpresa = "12345678000199",
                nomeFantasia = "Bad Co",
                razaoSocial = "Bad Co Ltda",
                cpfCnpj = "12345678000190",
                inscricaoEstadual = null,
                regimeTributario = null,
                bloqueado = false,
                createdAt = null,
            ),
        )

        assertEquals(IngestOutcome.FAILED, result.outcome)
        assertEquals("db down", result.errorMessage)
    }
}
