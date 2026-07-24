package br.com.vertice.emerion_dashboard.infrastructure.rest.notification.mapper

import br.com.vertice.emerion_dashboard.domain.notification.model.Notification
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationCategory
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationPriority
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationStatus
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationCategory as NotificationCategoryDto
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationPriority as NotificationPriorityDto
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationStatus as NotificationStatusDto
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import java.time.ZoneOffset

/** Maps between the domain model and the generated OpenAPI query DTOs. */
object NotificationQueryRestMapper {

    fun toResponse(notification: Notification): NotificationResponse =
        NotificationResponse(
            id = notification.id,
            userId = notification.userId,
            name = notification.name,
            description = notification.description,
            status = toDto(notification.status),
            category = toDto(notification.category),
            priority = toDto(notification.priority),
            referenceType = notification.referenceType,
            referenceId = notification.referenceId,
            actionUrl = notification.actionUrl,
            createdAt = notification.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = notification.updatedAt.atOffset(ZoneOffset.UTC),
            readAt = notification.readAt?.atOffset(ZoneOffset.UTC),
            dismissedAt = notification.dismissedAt?.atOffset(ZoneOffset.UTC),
        )

    fun toPageResponse(page: DomainPage<Notification>): NotificationPage =
        NotificationPage(
            data = page.content.map(::toResponse),
            pagination = PaginationInfo(
                total = page.totalElements,
                page = page.page,
                propertySize = page.size,
                totalPages = page.totalPages,
            ),
        )

    fun toDomain(status: NotificationStatusDto): NotificationStatus = when (status) {
        NotificationStatusDto.UNREAD -> NotificationStatus.UNREAD
        NotificationStatusDto.READ -> NotificationStatus.READ
        NotificationStatusDto.DISMISSED -> NotificationStatus.DISMISSED
    }

    fun toDomain(category: NotificationCategoryDto): NotificationCategory = when (category) {
        NotificationCategoryDto.INGESTION -> NotificationCategory.INGESTION
        NotificationCategoryDto.STATUS_UPDATE -> NotificationCategory.STATUS_UPDATE
        NotificationCategoryDto.APPROVAL_NEEDED -> NotificationCategory.APPROVAL_NEEDED
        NotificationCategoryDto.SYSTEM -> NotificationCategory.SYSTEM
    }

    private fun toDto(status: NotificationStatus): NotificationStatusDto = when (status) {
        NotificationStatus.UNREAD -> NotificationStatusDto.UNREAD
        NotificationStatus.READ -> NotificationStatusDto.READ
        NotificationStatus.DISMISSED -> NotificationStatusDto.DISMISSED
    }

    private fun toDto(category: NotificationCategory): NotificationCategoryDto = when (category) {
        NotificationCategory.INGESTION -> NotificationCategoryDto.INGESTION
        NotificationCategory.STATUS_UPDATE -> NotificationCategoryDto.STATUS_UPDATE
        NotificationCategory.APPROVAL_NEEDED -> NotificationCategoryDto.APPROVAL_NEEDED
        NotificationCategory.SYSTEM -> NotificationCategoryDto.SYSTEM
    }

    private fun toDto(priority: NotificationPriority): NotificationPriorityDto = when (priority) {
        NotificationPriority.LOW -> NotificationPriorityDto.LOW
        NotificationPriority.MEDIUM -> NotificationPriorityDto.MEDIUM
        NotificationPriority.HIGH -> NotificationPriorityDto.HIGH
    }
}
