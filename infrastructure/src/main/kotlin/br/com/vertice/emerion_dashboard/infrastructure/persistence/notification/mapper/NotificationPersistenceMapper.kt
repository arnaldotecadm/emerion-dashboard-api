package br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.mapper

import br.com.vertice.emerion_dashboard.domain.notification.model.Notification
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationCategory
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationPriority
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationStatus
import br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.model.NotificationCategoryJpa
import br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.model.NotificationJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.model.NotificationPriorityJpa
import br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.model.NotificationStatusJpa
import br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.projection.NotificationProjection

/** Maps between the domain model and the JPA entity/read projection. Kept out of the entity/domain classes on purpose. */
object NotificationPersistenceMapper {

    /** Read path: native-query projection (see `NotificationQueryRepository`) -> domain model. */
    fun toDomain(projection: NotificationProjection): Notification =
        Notification(
            id = projection.id,
            userId = projection.userId,
            name = projection.name,
            description = projection.description,
            status = NotificationStatus.valueOf(projection.status),
            category = NotificationCategory.valueOf(projection.category),
            priority = NotificationPriority.valueOf(projection.priority),
            referenceType = projection.referenceType,
            referenceId = projection.referenceId,
            actionUrl = projection.actionUrl,
            createdAt = projection.createdAt,
            updatedAt = projection.updatedAt,
            readAt = projection.readAt,
            dismissedAt = projection.dismissedAt,
        )

    /** Write path: JPA entity -> domain model. */
    fun toDomain(entity: NotificationJpaEntity): Notification =
        Notification(
            id = entity.id,
            userId = entity.userId,
            name = entity.name,
            description = entity.description,
            status = toDomainStatus(entity.status),
            category = toDomainCategory(entity.category),
            priority = toDomainPriority(entity.priority),
            referenceType = entity.referenceType,
            referenceId = entity.referenceId,
            actionUrl = entity.actionUrl,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            readAt = entity.readAt,
            dismissedAt = entity.dismissedAt,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun toEntity(domain: Notification, existing: NotificationJpaEntity?): NotificationJpaEntity =
        NotificationJpaEntity(
            id = existing?.id ?: domain.id,
            userId = domain.userId,
            name = domain.name,
            description = domain.description,
            status = toJpaStatus(domain.status),
            category = toJpaCategory(domain.category),
            priority = toJpaPriority(domain.priority),
            referenceType = domain.referenceType,
            referenceId = domain.referenceId,
            actionUrl = domain.actionUrl,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            readAt = domain.readAt,
            dismissedAt = domain.dismissedAt,
        )

    private fun toDomainStatus(status: NotificationStatusJpa): NotificationStatus = when (status) {
        NotificationStatusJpa.UNREAD -> NotificationStatus.UNREAD
        NotificationStatusJpa.READ -> NotificationStatus.READ
        NotificationStatusJpa.DISMISSED -> NotificationStatus.DISMISSED
    }

    private fun toJpaStatus(status: NotificationStatus): NotificationStatusJpa = when (status) {
        NotificationStatus.UNREAD -> NotificationStatusJpa.UNREAD
        NotificationStatus.READ -> NotificationStatusJpa.READ
        NotificationStatus.DISMISSED -> NotificationStatusJpa.DISMISSED
    }

    private fun toDomainCategory(category: NotificationCategoryJpa): NotificationCategory = when (category) {
        NotificationCategoryJpa.INGESTION -> NotificationCategory.INGESTION
        NotificationCategoryJpa.STATUS_UPDATE -> NotificationCategory.STATUS_UPDATE
        NotificationCategoryJpa.APPROVAL_NEEDED -> NotificationCategory.APPROVAL_NEEDED
        NotificationCategoryJpa.SYSTEM -> NotificationCategory.SYSTEM
    }

    private fun toJpaCategory(category: NotificationCategory): NotificationCategoryJpa = when (category) {
        NotificationCategory.INGESTION -> NotificationCategoryJpa.INGESTION
        NotificationCategory.STATUS_UPDATE -> NotificationCategoryJpa.STATUS_UPDATE
        NotificationCategory.APPROVAL_NEEDED -> NotificationCategoryJpa.APPROVAL_NEEDED
        NotificationCategory.SYSTEM -> NotificationCategoryJpa.SYSTEM
    }

    private fun toDomainPriority(priority: NotificationPriorityJpa): NotificationPriority = when (priority) {
        NotificationPriorityJpa.LOW -> NotificationPriority.LOW
        NotificationPriorityJpa.MEDIUM -> NotificationPriority.MEDIUM
        NotificationPriorityJpa.HIGH -> NotificationPriority.HIGH
    }

    private fun toJpaPriority(priority: NotificationPriority): NotificationPriorityJpa = when (priority) {
        NotificationPriority.LOW -> NotificationPriorityJpa.LOW
        NotificationPriority.MEDIUM -> NotificationPriorityJpa.MEDIUM
        NotificationPriority.HIGH -> NotificationPriorityJpa.HIGH
    }
}
