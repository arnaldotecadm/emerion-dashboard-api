package br.com.vertice.emerion_dashboard.infrastructure.persistence.notification

import br.com.vertice.emerion_dashboard.domain.notification.model.Notification
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationCategory
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationPriority
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationStatus
import br.com.vertice.emerion_dashboard.domain.notification.repository.NotificationRepository
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Exercises `NotificationRepositoryAdapter` against a real Postgres
 * instance, covering both the write path (JPA-entity-backed `save`) and the
 * read path (native-query + projection via `NotificationQueryRepository`),
 * including per-user scoping and the read/dismiss status transitions.
 */
@SpringBootTest
class NotificationRepositoryAdapterIntegrationTest(
    @Autowired private val notificationRepository: NotificationRepository,
) : PostgresIntegrationTest() {

    @Test
    fun `saves a notification and reads it back through the native query projection`() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val saved = notificationRepository.save(
            Notification.create(
                userId = "user-1",
                name = "Order needs approval",
                description = "Order #42 requires manager approval",
                category = NotificationCategory.APPROVAL_NEEDED,
                priority = NotificationPriority.HIGH,
                referenceType = "CustomerOrder",
                referenceId = "42",
                actionUrl = "/orders/42",
                now = now,
            ),
        )

        val found = notificationRepository.findByIdAndUserId(saved.id!!, "user-1")

        assertEquals(saved, found)
        assertEquals(NotificationStatus.UNREAD, found?.status)
    }

    @Test
    fun `does not find a notification belonging to a different user`() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val saved = notificationRepository.save(
            Notification.create(
                userId = "user-owner",
                name = "System notice",
                description = "Something happened",
                category = NotificationCategory.SYSTEM,
                now = now,
            ),
        )

        val found = notificationRepository.findByIdAndUserId(saved.id!!, "someone-else")

        assertNull(found)
    }

    @Test
    fun `searches notifications scoped by userId and filtered by status and category`() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        notificationRepository.save(
            Notification.create(
                userId = "user-2",
                name = "Ingestion complete",
                description = "Batch ingested successfully",
                category = NotificationCategory.INGESTION,
                now = now,
            ),
        )
        val matching = notificationRepository.save(
            Notification.create(
                userId = "user-2",
                name = "Approval needed",
                description = "Order #7 requires approval",
                category = NotificationCategory.APPROVAL_NEEDED,
                now = now,
            ),
        )
        notificationRepository.save(
            Notification.create(
                userId = "other-user",
                name = "Approval needed",
                description = "Order #8 requires approval",
                category = NotificationCategory.APPROVAL_NEEDED,
                now = now,
            ),
        )

        val page = notificationRepository.findAll(
            pageRequest = PageRequest(page = 0, size = 10),
            userId = "user-2",
            status = NotificationStatus.UNREAD,
            category = NotificationCategory.APPROVAL_NEEDED,
        )

        assertEquals(listOf(matching.id), page.content.map { it.id })
    }

    @Test
    fun `marking as read and dismissing persist through save`() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val saved = notificationRepository.save(
            Notification.create(
                userId = "user-3",
                name = "Status update",
                description = "Order #9 shipped",
                category = NotificationCategory.STATUS_UPDATE,
                now = now,
            ),
        )

        val read = notificationRepository.save(saved.markAsRead(now.plusSeconds(60)))
        assertEquals(NotificationStatus.READ, read.status)
        assertEquals(now.plusSeconds(60), read.readAt)

        val dismissed = notificationRepository.save(read.dismiss(now.plusSeconds(120)))
        assertEquals(NotificationStatus.DISMISSED, dismissed.status)
        assertEquals(now.plusSeconds(120), dismissed.dismissedAt)

        val found = notificationRepository.findByIdAndUserId(saved.id!!, "user-3")
        assertEquals(NotificationStatus.DISMISSED, found?.status)
    }
}
