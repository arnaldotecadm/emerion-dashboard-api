package br.com.vertice.emerion_dashboard.application.product.ingestion

import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestOutcome
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestProductCommand
import br.com.vertice.emerion_dashboard.domain.product.model.Product
import br.com.vertice.emerion_dashboard.domain.product.repository.ProductRepository
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

class IngestProductsServiceTest {

    private val fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private val productRepository = mockk<ProductRepository>()
    private val service = IngestProductsService(productRepository, fixedClock)

    @Test
    fun `creates a new product when externalId is not known yet`() {
        every { productRepository.findByExternalId("FB-1") } returns null
        val savedSlot = slot<Product>()
        every { productRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-1",
                items = listOf(
                    IngestProductCommand(
                        externalId = "FB-1",
                        nome = "Widget",
                        preco = BigDecimal("19.90"),
                    ),
                ),
            ),
        )

        assertEquals(1, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(0, result.totalFailed)
        assertEquals(IngestOutcome.CREATED, result.results.single().outcome)
        assertEquals("FB-1", savedSlot.captured.externalId)
        verify(exactly = 1) { productRepository.save(any()) }
    }

    @Test
    fun `updates an existing product when externalId is already known (idempotent re-run)`() {
        val existing = Product(
            id = 42L,
            externalId = "FB-2",
            nome = "Old Name",
            preco = BigDecimal("10.00"),
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z"),
        )
        every { productRepository.findByExternalId("FB-2") } returns existing
        every { productRepository.save(any()) } answers { firstArg() }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-2",
                items = listOf(
                    IngestProductCommand(
                        externalId = "FB-2",
                        nome = "New Name",
                        preco = BigDecimal("25.50"),
                    ),
                ),
            ),
        )

        assertEquals(IngestOutcome.UPDATED, result.results.single().outcome)
        verify(exactly = 1) { productRepository.save(match { it.id == 42L && it.nome == "New Name" }) }
    }

    @Test
    fun `records a failure for one item without aborting the rest of the batch`() {
        every { productRepository.findByExternalId("FB-OK") } returns null
        every { productRepository.findByExternalId("FB-BAD") } throws RuntimeException("db down")
        every { productRepository.save(any()) } answers { firstArg<Product>().copy(id = 1L) }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-3",
                items = listOf(
                    IngestProductCommand("FB-OK", "Ok Product", BigDecimal("5.00")),
                    IngestProductCommand("FB-BAD", "Bad Product", null),
                ),
            ),
        )

        assertEquals(2, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(1, result.totalFailed)
    }

    @Test
    fun `ingestSingle creates a new product when externalId is not known yet`() {
        every { productRepository.findByExternalId("FB-4") } returns null
        val savedSlot = slot<Product>()
        every { productRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingestSingle(
            IngestProductCommand(
                externalId = "FB-4",
                nome = "Widget",
                preco = null,
            ),
        )

        assertEquals(IngestOutcome.CREATED, result.outcome)
        assertEquals("FB-4", result.externalId)
        verify(exactly = 1) { productRepository.save(any()) }
    }

    @Test
    fun `ingestSingle reports a failure without throwing when the item fails`() {
        every { productRepository.findByExternalId("FB-5") } throws RuntimeException("db down")

        val result = service.ingestSingle(
            IngestProductCommand("FB-5", "Bad Product", null),
        )

        assertEquals(IngestOutcome.FAILED, result.outcome)
        assertEquals("db down", result.errorMessage)
    }
}
