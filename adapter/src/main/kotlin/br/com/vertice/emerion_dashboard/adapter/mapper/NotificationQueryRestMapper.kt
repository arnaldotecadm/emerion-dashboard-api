package br.com.vertice.emerion_dashboard.adapter.mapper

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

    fun Notification.toResponse(): NotificationResponse =
        NotificationResponse(
            id = id,
            userId = userId,
            name = name,
            description = description,
            status = status.toDto(),
            category = category.toDto(),
            priority = priority.toDto(),
            referenceType = referenceType,
            referenceId = referenceId,
            actionUrl = actionUrl,
            createdAt = createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = updatedAt.atOffset(ZoneOffset.UTC),
            readAt = readAt?.atOffset(ZoneOffset.UTC),
            dismissedAt = dismissedAt?.atOffset(ZoneOffset.UTC),
        )

    fun DomainPage<Notification>.toPageResponse(): NotificationPage =
        NotificationPage(
            data = content.map { it.toResponse() },
            pagination = PaginationInfo(
                total = totalElements,
                page = page,
                propertySize = size,
                totalPages = totalPages,
            ),
        )

    fun NotificationStatusDto.toDomain(): NotificationStatus = when (this) {
        NotificationStatusDto.UNREAD -> NotificationStatus.UNREAD
        NotificationStatusDto.READ -> NotificationStatus.READ
        NotificationStatusDto.DISMISSED -> NotificationStatus.DISMISSED
    }

    fun NotificationCategoryDto.toDomain(): NotificationCategory = when (this) {
        NotificationCategoryDto.INGESTION -> NotificationCategory.INGESTION
        NotificationCategoryDto.STATUS_UPDATE -> NotificationCategory.STATUS_UPDATE
        NotificationCategoryDto.APPROVAL_NEEDED -> NotificationCategory.APPROVAL_NEEDED
        NotificationCategoryDto.SYSTEM -> NotificationCategory.SYSTEM
    }

    private fun NotificationStatus.toDto(): NotificationStatusDto = when (this) {
        NotificationStatus.UNREAD -> NotificationStatusDto.UNREAD
        NotificationStatus.READ -> NotificationStatusDto.READ
        NotificationStatus.DISMISSED -> NotificationStatusDto.DISMISSED
    }

    private fun NotificationCategory.toDto(): NotificationCategoryDto = when (this) {
        NotificationCategory.INGESTION -> NotificationCategoryDto.INGESTION
        NotificationCategory.STATUS_UPDATE -> NotificationCategoryDto.STATUS_UPDATE
        NotificationCategory.APPROVAL_NEEDED -> NotificationCategoryDto.APPROVAL_NEEDED
        NotificationCategory.SYSTEM -> NotificationCategoryDto.SYSTEM
    }

    private fun NotificationPriority.toDto(): NotificationPriorityDto = when (this) {
        NotificationPriority.LOW -> NotificationPriorityDto.LOW
        NotificationPriority.MEDIUM -> NotificationPriorityDto.MEDIUM
        NotificationPriority.HIGH -> NotificationPriorityDto.HIGH
    }
}
