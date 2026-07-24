package br.com.vertice.emerion_dashboard.application.customerorder.ingestion

import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderItemCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestOutcome
import br.com.vertice.emerion_dashboard.application.notification.creation.CreateNotificationUseCase
import br.com.vertice.emerion_dashboard.application.notification.creation.model.CreateNotificationCommand
import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoUser
import br.com.vertice.emerion_dashboard.domain.cognitouser.repository.CognitoUserRepository
import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrder
import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrderItem
import br.com.vertice.emerion_dashboard.domain.customerorder.repository.CustomerOrderRepository
import br.com.vertice.emerion_dashboard.domain.notification.model.Notification
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationCategory
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

class IngestCustomerOrdersServiceTest {

    private val fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private val customerOrderRepository = mockk<CustomerOrderRepository>()
    private val cognitoUserRepository = mockk<CognitoUserRepository>()
    private val createNotificationUseCase = mockk<CreateNotificationUseCase>()
    private val service = IngestCustomerOrdersService(
        customerOrderRepository = customerOrderRepository,
        cognitoUserRepository = cognitoUserRepository,
        createNotificationUseCase = createNotificationUseCase,
        clock = fixedClock,
    )

    private fun itemCommand(produto: String) = IngestCustomerOrderItemCommand(
        produto = produto,
        descricao = "Produto de teste",
        quantidade = BigDecimal("2"),
        valorUnitario = BigDecimal("10.00"),
        valorTotal = BigDecimal("20.00"),
        seqRe2 = 1,
    )

    private fun orderCommand(externalId: String, itens: List<IngestCustomerOrderItemCommand>) = IngestCustomerOrderCommand(
        externalId = externalId,
        codCli = "100",
        cnpjEmpresa = "12345678000190",
        cpfCnpj = null,
        nronfe = "NF-1",
        dteres = LocalDate.parse("2025-06-01"),
        sitres = "FATURADO",
        totger = BigDecimal("20.00"),
        totres = BigDecimal("20.00"),
        totipi = BigDecimal.ZERO,
        totsub = BigDecimal.ZERO,
        totdescinc = BigDecimal.ZERO,
        itens = itens,
    )

    @Test
    fun `creates a new order and creates notifications for active users`() {
        every { customerOrderRepository.findByExternalId("NUM-1") } returns null
        val savedSlot = slot<CustomerOrder>()
        every { customerOrderRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }
        every { cognitoUserRepository.findAll() } returns listOf(
            cognitoUser(sub = "active-1", enabled = true),
            cognitoUser(sub = "inactive-1", enabled = false),
            cognitoUser(sub = "active-2", enabled = true),
        )
        every { createNotificationUseCase.create(any()) } answers {
            Notification.create(
                userId = firstArg<CreateNotificationCommand>().userId,
                name = "n",
                description = "d",
                category = NotificationCategory.INGESTION,
                now = Instant.parse("2026-01-01T00:00:00Z"),
            )
        }

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-1",
                items = listOf(orderCommand("NUM-1", listOf(itemCommand("1.1.1")))),
            ),
        )

        assertEquals(1, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(0, result.totalFailed)
        assertEquals(IngestOutcome.CREATED, result.results.single().outcome)
        assertEquals("NUM-1", savedSlot.captured.externalId)
        assertEquals(1, savedSlot.captured.itens.size)
        verify(exactly = 1) { customerOrderRepository.save(any()) }
        verify(exactly = 2) {
            createNotificationUseCase.create(match { it.userId == "active-1" || it.userId == "active-2" })
        }
        verify(exactly = 0) { createNotificationUseCase.create(match { it.userId == "inactive-1" }) }
    }

    @Test
    fun `updates an existing order when externalId is already known (idempotent re-run)`() {
        val existing = CustomerOrder(
            id = 42L,
            externalId = "NUM-2",
            codCli = "100",
            cnpjEmpresa = "12345678000199",
            cpfCnpj = null,
            nronfe = null,
            dteres = LocalDate.parse("2025-01-01"),
            sitres = "ABERTO",
            totger = BigDecimal("10.00"),
            totres = BigDecimal("10.00"),
            totipi = BigDecimal.ZERO,
            totsub = BigDecimal.ZERO,
            totdescinc = BigDecimal.ZERO,
            itens = listOf(
                CustomerOrderItem(
                    produto = "1.1.1",
                    descricao = "Old",
                    quantidade = BigDecimal.ONE,
                    valorUnitario = BigDecimal.TEN,
                    valorTotal = BigDecimal.TEN,
                    seqRe2 = 1,
                ),
            ),
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z"),
        )
        every { customerOrderRepository.findByExternalId("NUM-2") } returns existing
        every { customerOrderRepository.save(any()) } answers { firstArg() }
        every { cognitoUserRepository.findAll() } returns listOf(cognitoUser(sub = "active-1", enabled = true))

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-2",
                items = listOf(orderCommand("NUM-2", listOf(itemCommand("1.1.1"), itemCommand("2.2.2")))),
            ),
        )

        assertEquals(IngestOutcome.UPDATED, result.results.single().outcome)
        verify(exactly = 1) { customerOrderRepository.save(match { it.id == 42L && it.itens.size == 2 && it.sitres == "FATURADO" }) }
        verify(exactly = 0) { createNotificationUseCase.create(any()) }
    }

    @Test
    fun `records a failure for one item without aborting the rest of the batch`() {
        every { customerOrderRepository.findByExternalId("OK") } returns null
        every { customerOrderRepository.findByExternalId("BAD") } throws RuntimeException("db down")
        every { customerOrderRepository.save(any()) } answers { firstArg<CustomerOrder>().copy(id = 1L) }
        every { cognitoUserRepository.findAll() } returns emptyList()

        val result = service.ingest(
            IngestBatchCommand(
                batchId = "batch-3",
                items = listOf(
                    orderCommand("OK", listOf(itemCommand("1.1.1"))),
                    orderCommand("BAD", listOf(itemCommand("1.1.1"))),
                ),
            ),
        )

        assertEquals(2, result.totalReceived)
        assertEquals(1, result.totalSucceeded)
        assertEquals(1, result.totalFailed)
        verify(exactly = 0) { createNotificationUseCase.create(any()) }
    }

    @Test
    fun `ingestSingle creates a new order when externalId is not known yet`() {
        every { customerOrderRepository.findByExternalId("NUM-4") } returns null
        val savedSlot = slot<CustomerOrder>()
        every { customerOrderRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }
        every { cognitoUserRepository.findAll() } returns emptyList()

        val result = service.ingestSingle(orderCommand("NUM-4", listOf(itemCommand("1.1.1"))))

        assertEquals(IngestOutcome.CREATED, result.outcome)
        assertEquals("NUM-4", result.externalId)
        verify(exactly = 1) { customerOrderRepository.save(any()) }
    }

    @Test
    fun `ingestSingle reports a failure without throwing when the item fails`() {
        every { customerOrderRepository.findByExternalId("NUM-5") } throws RuntimeException("db down")
        every { cognitoUserRepository.findAll() } returns emptyList()

        val result = service.ingestSingle(orderCommand("NUM-5", listOf(itemCommand("1.1.1"))))

        assertEquals(IngestOutcome.FAILED, result.outcome)
        assertEquals("db down", result.errorMessage)
    }

    private fun cognitoUser(sub: String, enabled: Boolean) = CognitoUser(
        id = null,
        sub = sub,
        username = sub,
        email = null,
        enabled = enabled,
        groups = emptyList(),
        lastSyncedAt = Instant.parse("2026-01-01T00:00:00Z"),
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-01T00:00:00Z"),
    )
}
