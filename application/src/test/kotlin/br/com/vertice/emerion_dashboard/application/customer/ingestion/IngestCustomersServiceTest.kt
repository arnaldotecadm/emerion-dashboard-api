package br.com.vertice.emerion_dashboard.application.customer.ingestion

import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestCustomerCommand
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestOutcome
import br.com.vertice.emerion_dashboard.domain.customer.model.Customer
import br.com.vertice.emerion_dashboard.domain.customer.repository.CustomerRepository
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
    private val customerRepository = mockk<CustomerRepository>()
    private val service = IngestCustomersService(customerRepository, fixedClock)

    @Test
    fun `creates a new customer when externalId is not known yet`() {
        every { customerRepository.findByExternalId("FB-1") } returns null
        val savedSlot = slot<Customer>()
        every { customerRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-1",
                items = listOf(
                    IngestCustomerCommand(
                        externalId = "FB-1",
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
        assertEquals(IngestOutcome.CREATED, result.results.single().outcome)
        assertEquals("FB-1", savedSlot.captured.externalId)
        verify(exactly = 1) { customerRepository.save(any()) }
    }

    @Test
    fun `updates an existing customer when externalId is already known (idempotent re-run)`() {
        val existing = Customer(
            id = 42L,
            externalId = "FB-2",
            nomeFantasia = "Old Name",
            razaoSocial = "Old Razao Social Ltda",
            cpfCnpj = "12345678000190",
            inscricaoEstadual = null,
            regimeTributario = null,
            bloqueado = true,
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z"),
        )
        every { customerRepository.findByExternalId("FB-2") } returns existing
        every { customerRepository.save(any()) } answers { firstArg() }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-2",
                items = listOf(
                    IngestCustomerCommand(
                        externalId = "FB-2",
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

        assertEquals(IngestOutcome.UPDATED, result.results.single().outcome)
        verify(exactly = 1) { customerRepository.save(match { it.id == 42L && it.nomeFantasia == "New Name" }) }
    }

    @Test
    fun `records a failure for one item without aborting the rest of the batch`() {
        every { customerRepository.findByExternalId("FB-OK") } returns null
        every { customerRepository.findByExternalId("FB-BAD") } throws RuntimeException("db down")
        every { customerRepository.save(any()) } answers { firstArg<Customer>().copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-3",
                items = listOf(
                    IngestCustomerCommand("FB-OK", "Ok Co", "Ok Co Ltda", "12345678000190", null, null, false, null),
                    IngestCustomerCommand("FB-BAD", "Bad Co", "Bad Co Ltda", "12345678000190", null, null, false, null),
                ),
            ),
        )

        assertEquals(2, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(1, result.totalFailed)
    }

    @Test
    fun `ingestSingle creates a new customer when externalId is not known yet`() {
        every { customerRepository.findByExternalId("FB-4") } returns null
        val savedSlot = slot<Customer>()
        every { customerRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingestSingle(
            IngestCustomerCommand(
                externalId = "FB-4",
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
        assertEquals("FB-4", result.externalId)
        verify(exactly = 1) { customerRepository.save(any()) }
    }

    @Test
    fun `ingestSingle reports a failure without throwing when the item fails`() {
        every { customerRepository.findByExternalId("FB-5") } throws RuntimeException("db down")

        val result = service.ingestSingle(
            IngestCustomerCommand("FB-5", "Bad Co", "Bad Co Ltda", "12345678000190", null, null, false, null),
        )

        assertEquals(IngestOutcome.FAILED, result.outcome)
        assertEquals("db down", result.errorMessage)
    }
}
