package br.com.vertice.emerion_dashboard.application.notification.query

import br.com.vertice.emerion_dashboard.application.notification.query.model.ListNotificationsQuery
import br.com.vertice.emerion_dashboard.domain.notification.exception.NotificationNotFoundException
import br.com.vertice.emerion_dashboard.domain.notification.model.Notification
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationCategory
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationPriority
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationStatus
import br.com.vertice.emerion_dashboard.domain.notification.repository.NotificationRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NotificationQueryServiceTest {

    private val fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private val notificationRepository = mockk<NotificationRepository>()
    private val service = NotificationQueryService(notificationRepository, fixedClock)

    private val existing = Notification(
        id = 1L,
        userId = "cognito-sub-123",
        name = "Order needs approval",
        description = "Order #42 requires manager approval",
        status = NotificationStatus.UNREAD,
        category = NotificationCategory.APPROVAL_NEEDED,
        priority = NotificationPriority.HIGH,
        referenceType = "CustomerOrder",
        referenceId = "42",
        actionUrl = "/orders/42",
        createdAt = Instant.parse("2025-12-31T00:00:00Z"),
        updatedAt = Instant.parse("2025-12-31T00:00:00Z"),
        readAt = null,
        dismissedAt = null,
    )

    @Test
    fun `getById returns the notification when it belongs to the caller`() {
        every { notificationRepository.findByIdAndUserId(1L, "cognito-sub-123") } returns existing

        val result = service.getById(1L, "cognito-sub-123")

        assertEquals(existing, result)
    }

    @Test
    fun `getById throws NotificationNotFoundException when not found for the caller`() {
        every { notificationRepository.findByIdAndUserId(1L, "cognito-sub-123") } returns null

        assertFailsWith<NotificationNotFoundException> { service.getById(1L, "cognito-sub-123") }
    }

    @Test
    fun `list delegates to the repository with the caller's userId and filters`() {
        every {
            notificationRepository.findAll(PageRequest(0, 20), "cognito-sub-123", NotificationStatus.UNREAD, null)
        } returns Page(content = listOf(existing), page = 0, size = 20, totalElements = 1)

        val result = service.list(
            ListNotificationsQuery(page = 0, size = 20, userId = "cognito-sub-123", status = NotificationStatus.UNREAD, category = null),
        )

        assertEquals(1, result.totalElements)
        assertEquals(existing, result.content.single())
    }

    @Test
    fun `markAsRead transitions status to READ and stamps readAt`() {
        every { notificationRepository.findByIdAndUserId(1L, "cognito-sub-123") } returns existing
        val savedSlot = slot<Notification>()
        every { notificationRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

        val result = service.markAsRead(1L, "cognito-sub-123")

        assertEquals(NotificationStatus.READ, result.status)
        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), result.readAt)
        verify(exactly = 1) { notificationRepository.save(any()) }
    }

    @Test
    fun `dismiss transitions status to DISMISSED and stamps dismissedAt`() {
        every { notificationRepository.findByIdAndUserId(1L, "cognito-sub-123") } returns existing
        val savedSlot = slot<Notification>()
        every { notificationRepository.save(capture(savedSlot)) } answers { savedSlot.captured }

        val result = service.dismiss(1L, "cognito-sub-123")

        assertEquals(NotificationStatus.DISMISSED, result.status)
        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), result.dismissedAt)
        verify(exactly = 1) { notificationRepository.save(any()) }
    }
}
