package br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

/**
 * JPA entity for the `notification` table. Lives entirely in the
 * infrastructure layer: the domain layer never sees this class, only
 * `domain.notification.model.Notification` via
 * `NotificationPersistenceMapper`.
 */
@Entity
@Table(name = "notification")
class NotificationJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "user_id", nullable = false)
    var userId: String = "",

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "description", nullable = false)
    var description: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: NotificationStatusJpa = NotificationStatusJpa.UNREAD,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    var category: NotificationCategoryJpa = NotificationCategoryJpa.SYSTEM,

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    var priority: NotificationPriorityJpa = NotificationPriorityJpa.MEDIUM,

    @Column(name = "reference_type")
    var referenceType: String? = null,

    @Column(name = "reference_id")
    var referenceId: String? = null,

    @Column(name = "action_url")
    var actionUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Column(name = "read_at")
    var readAt: Instant? = null,

    @Column(name = "dismissed_at")
    var dismissedAt: Instant? = null,
)
