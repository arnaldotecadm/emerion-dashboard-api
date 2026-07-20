package br.com.vertice.emerion_dashboard.application.customer

import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.domain.customer.CustomerRepository
import br.com.vertice.emerion_dashboard.domain.customer.CustomerStatus
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
                        name = "Acme Corp",
                        email = "billing@acme.test",
                        status = CustomerStatus.ACTIVE,
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
            name = "Old Name",
            email = null,
            status = CustomerStatus.INACTIVE,
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
                        name = "New Name",
                        email = "new@acme.test",
                        status = CustomerStatus.ACTIVE,
                        createdAt = null,
                    ),
                ),
            ),
        )

        assertEquals(IngestOutcome.UPDATED, result.results.single().outcome)
        verify(exactly = 1) { customerRepository.save(match { it.id == 42L && it.name == "New Name" }) }
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
                    IngestCustomerCommand("FB-OK", "Ok Co", null, CustomerStatus.ACTIVE, null),
                    IngestCustomerCommand("FB-BAD", "Bad Co", null, CustomerStatus.ACTIVE, null),
                ),
            ),
        )

        assertEquals(2, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(1, result.totalFailed)
    }
}
