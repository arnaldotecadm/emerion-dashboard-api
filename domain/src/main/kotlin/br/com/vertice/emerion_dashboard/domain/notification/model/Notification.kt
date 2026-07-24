package br.com.vertice.emerion_dashboard.domain.notification.model

import java.time.Instant

/**
 * Domain model for a Notification. Plain Kotlin, no JPA/Jakarta/OpenAPI
 * annotations. Unlike every other resource in this API, notifications are
 * never ingested from emerion-load-service — they are created internally by
 * this application's own business logic (e.g. an order needing approval,
 * an ingestion outcome worth surfacing) and are always scoped to a single
 * Cognito user (`userId`, the token's `sub` claim).
 */
data class Notification(
    val id: Long?,
    val userId: String,
    val name: String,
    val description: String,
    val status: NotificationStatus,
    val category: NotificationCategory,
    val priority: NotificationPriority,
    val referenceType: String?,
    val referenceId: String?,
    val actionUrl: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val readAt: Instant?,
    val dismissedAt: Instant?,
) {
    companion object {
        /** Factory for a brand-new notification (always starts UNREAD). */
        fun create(
            userId: String,
            name: String,
            description: String,
            category: NotificationCategory,
            priority: NotificationPriority = NotificationPriority.MEDIUM,
            referenceType: String? = null,
            referenceId: String? = null,
            actionUrl: String? = null,
            now: Instant,
        ) = Notification(
            id = null,
            userId = userId,
            name = name,
            description = description,
            status = NotificationStatus.UNREAD,
            category = category,
            priority = priority,
            referenceType = referenceType,
            referenceId = referenceId,
            actionUrl = actionUrl,
            createdAt = now,
            updatedAt = now,
            readAt = null,
            dismissedAt = null,
        )
    }

    /** Marks the notification as read, bumping updatedAt. Idempotent: keeps the first readAt. */
    fun markAsRead(now: Instant) = copy(
        status = NotificationStatus.READ,
        readAt = readAt ?: now,
        updatedAt = now,
    )

    /** Dismisses the notification, bumping updatedAt. */
    fun dismiss(now: Instant) = copy(
        status = NotificationStatus.DISMISSED,
        dismissedAt = now,
        updatedAt = now,
    )
}
