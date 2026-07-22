package br.com.vertice.emerion_dashboard.application.vendedor.ingestion

import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestOutcome
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestVendedorCommand
import br.com.vertice.emerion_dashboard.domain.vendedor.model.Vendedor
import br.com.vertice.emerion_dashboard.domain.vendedor.repository.VendedorRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.test.assertEquals

class IngestVendedoresServiceTest {

    private val fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private val vendedorRepository = mockk<VendedorRepository>()
    private val service = IngestVendedoresService(vendedorRepository, fixedClock)

    private fun command(externalId: String, nome: String) = IngestVendedorCommand(
        externalId = externalId,
        nome = nome,
        apelido = "Apelido",
        cpfCnpj = "12345678900",
        telefone = "1111-1111",
        celular = "99999-9999",
        email = "vendedor@example.com",
        cidade = "Sao Paulo",
        uf = "SP",
        situacao = "ATIVO",
        saldo = BigDecimal("100.00"),
        dataCadastro = LocalDate.parse("2025-01-01"),
    )

    @Test
    fun `creates a new vendedor when externalId is not known yet`() {
        every { vendedorRepository.findByExternalId("FB-1") } returns null
        val savedSlot = slot<Vendedor>()
        every { vendedorRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-1",
                items = listOf(command("FB-1", "Fulano")),
            ),
        )

        assertEquals(1, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(0, result.totalFailed)
        assertEquals(IngestOutcome.CREATED, result.results.single().outcome)
        assertEquals("FB-1", savedSlot.captured.externalId)
        verify(exactly = 1) { vendedorRepository.save(any()) }
    }

    @Test
    fun `updates an existing vendedor when externalId is already known (idempotent re-run)`() {
        val existing = Vendedor(
            id = 42L,
            externalId = "FB-2",
            nome = "Old Name",
            apelido = null,
            cpfCnpj = null,
            telefone = null,
            celular = null,
            email = null,
            cidade = null,
            uf = null,
            situacao = "ATIVO",
            saldo = BigDecimal("10.00"),
            dataCadastro = null,
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z"),
        )
        every { vendedorRepository.findByExternalId("FB-2") } returns existing
        every { vendedorRepository.save(any()) } answers { firstArg() }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-2",
                items = listOf(command("FB-2", "New Name")),
            ),
        )

        assertEquals(IngestOutcome.UPDATED, result.results.single().outcome)
        verify(exactly = 1) { vendedorRepository.save(match { it.id == 42L && it.nome == "New Name" }) }
    }

    @Test
    fun `records a failure for one item without aborting the rest of the batch`() {
        every { vendedorRepository.findByExternalId("FB-OK") } returns null
        every { vendedorRepository.findByExternalId("FB-BAD") } throws RuntimeException("db down")
        every { vendedorRepository.save(any()) } answers { firstArg<Vendedor>().copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-3",
                items = listOf(command("FB-OK", "Ok Vendedor"), command("FB-BAD", "Bad Vendedor")),
            ),
        )

        assertEquals(2, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(1, result.totalFailed)
    }

    @Test
    fun `ingestSingle creates a new vendedor when externalId is not known yet`() {
        every { vendedorRepository.findByExternalId("FB-4") } returns null
        val savedSlot = slot<Vendedor>()
        every { vendedorRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingestSingle(command("FB-4", "Fulano"))

        assertEquals(IngestOutcome.CREATED, result.outcome)
        assertEquals("FB-4", result.externalId)
        verify(exactly = 1) { vendedorRepository.save(any()) }
    }

    @Test
    fun `ingestSingle reports a failure without throwing when the item fails`() {
        every { vendedorRepository.findByExternalId("FB-5") } throws RuntimeException("db down")

        val result = service.ingestSingle(command("FB-5", "Bad Vendedor"))

        assertEquals(IngestOutcome.FAILED, result.outcome)
        assertEquals("db down", result.errorMessage)
    }
}
