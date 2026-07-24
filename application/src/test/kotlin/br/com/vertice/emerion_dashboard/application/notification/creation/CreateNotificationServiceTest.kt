package br.com.vertice.emerion_dashboard.application.notification.creation

import br.com.vertice.emerion_dashboard.application.notification.creation.model.CreateNotificationCommand
import br.com.vertice.emerion_dashboard.domain.notification.model.Notification
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationCategory
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationPriority
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationStatus
import br.com.vertice.emerion_dashboard.domain.notification.repository.NotificationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals

class CreateNotificationServiceTest {

    private val fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private val notificationRepository = mockk<NotificationRepository>()
    private val service = CreateNotificationService(notificationRepository, fixedClock)

    @Test
    fun `creates a brand-new UNREAD notification for the given user`() {
        val savedSlot = slot<Notification>()
        every { notificationRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.create(
            CreateNotificationCommand(
                userId = "cognito-sub-123",
                name = "Order needs approval",
                description = "Order #42 requires manager approval",
                category = NotificationCategory.APPROVAL_NEEDED,
                priority = NotificationPriority.HIGH,
                referenceType = "CustomerOrder",
                referenceId = "42",
                actionUrl = "/orders/42",
            ),
        )

        assertEquals(1L, result.id)
        assertEquals("cognito-sub-123", savedSlot.captured.userId)
        assertEquals(NotificationStatus.UNREAD, savedSlot.captured.status)
        assertEquals(NotificationCategory.APPROVAL_NEEDED, savedSlot.captured.category)
        assertEquals(NotificationPriority.HIGH, savedSlot.captured.priority)
        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), savedSlot.captured.createdAt)
        verify(exactly = 1) { notificationRepository.save(any()) }
    }

    @Test
    fun `defaults priority to MEDIUM when not specified`() {
        every { notificationRepository.save(any()) } answers { firstArg() }

        val result = service.create(
            CreateNotificationCommand(
                userId = "cognito-sub-123",
                name = "Batch ingested",
                description = "100 customer orders ingested",
                category = NotificationCategory.INGESTION,
            ),
        )

        assertEquals(NotificationPriority.MEDIUM, result.priority)
    }
}
