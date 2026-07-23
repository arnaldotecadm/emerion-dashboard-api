package br.com.vertice.emerion_dashboard.application.customercredit.ingestion

import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestCustomerCreditCommand
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestOutcome
import br.com.vertice.emerion_dashboard.domain.customercredit.model.CustomerCredit
import br.com.vertice.emerion_dashboard.domain.customercredit.repository.CustomerCreditRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals

class IngestCustomerCreditsServiceTest {

    private val fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private val customerCreditRepository = mockk<CustomerCreditRepository>()
    private val service = IngestCustomerCreditsService(customerCreditRepository, fixedClock)

    private fun command(customerExternalId: String, sequencia: String?) = IngestCustomerCreditCommand(
        customerExternalId = customerExternalId,
        cnpjEmpresa = "12345678000199",
        sequencia = sequencia,
        data = Instant.parse("2025-06-01T00:00:00Z"),
        dataPedido = null,
        valorUtilizado = BigDecimal("100.00"),
        valorTotal = BigDecimal("500.00"),
        saldo = BigDecimal("400.00"),
        situacao = "ABERTO",
        tipo = "SAIDA",
    )

    @Test
    fun `creates a new credit entry when the (customerExternalId, sequencia) key is not known yet`() {
        every { customerCreditRepository.findByCustomerExternalIdAndSequencia("100", "1") } returns null
        val savedSlot = slot<CustomerCredit>()
        every { customerCreditRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(batchId = "batch-1", items = listOf(command("100", "1"))),
        )

        assertEquals(1, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(0, result.totalFailed)
        assertEquals(IngestOutcome.CREATED, result.results.single().outcome)
        assertEquals("100", savedSlot.captured.customerExternalId)
        verify(exactly = 1) { customerCreditRepository.save(any()) }
    }

    @Test
    fun `updates an existing credit entry when the (customerExternalId, sequencia) key is already known (idempotent re-run)`() {
        val existing = CustomerCredit(
            id = 42L,
            customerExternalId = "200",
            cnpjEmpresa = "12345678000199",
            sequencia = "5",
            data = Instant.parse("2025-01-01T00:00:00Z"),
            dataPedido = null,
            valorUtilizado = BigDecimal("50.00"),
            valorTotal = BigDecimal("500.00"),
            saldo = BigDecimal("450.00"),
            situacao = "ABERTO",
            tipo = "SAIDA",
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z"),
        )
        every { customerCreditRepository.findByCustomerExternalIdAndSequencia("200", "5") } returns existing
        every { customerCreditRepository.save(any()) } answers { firstArg() }

        val result = service.ingest(
            IngestBatchCommand(batchId = "batch-2", items = listOf(command("200", "5"))),
        )

        assertEquals(IngestOutcome.UPDATED, result.results.single().outcome)
        verify(exactly = 1) { customerCreditRepository.save(match { it.id == 42L && it.valorUtilizado == BigDecimal("100.00") }) }
    }

    @Test
    fun `always inserts a new entry when sequencia is null since there is no reliable key`() {
        val savedSlot = slot<CustomerCredit>()
        every { customerCreditRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingestSingle(command("300", null))

        assertEquals(IngestOutcome.CREATED, result.outcome)
        verify(exactly = 0) { customerCreditRepository.findByCustomerExternalIdAndSequencia(any(), any()) }
        verify(exactly = 1) { customerCreditRepository.save(any()) }
    }

    @Test
    fun `records a failure for one item without aborting the rest of the batch`() {
        every { customerCreditRepository.findByCustomerExternalIdAndSequencia("OK", "1") } returns null
        every { customerCreditRepository.findByCustomerExternalIdAndSequencia("BAD", "1") } throws RuntimeException("db down")
        every { customerCreditRepository.save(any()) } answers { firstArg<CustomerCredit>().copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-3",
                items = listOf(command("OK", "1"), command("BAD", "1")),
            ),
        )

        assertEquals(2, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(1, result.totalFailed)
    }

    @Test
    fun `ingestSingle reports a failure without throwing when the item fails`() {
        every { customerCreditRepository.findByCustomerExternalIdAndSequencia("400", "1") } throws RuntimeException("db down")

        val result = service.ingestSingle(command("400", "1"))

        assertEquals(IngestOutcome.FAILED, result.outcome)
        assertEquals("db down", result.errorMessage)
    }
}
