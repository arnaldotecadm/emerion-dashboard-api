package br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.projection

import java.time.Instant

/**
 * Read-side projection for `notification`, populated straight from a native
 * SQL result set (see `NotificationQueryRepository`) instead of a JPA
 * entity. Enum columns are exposed as plain strings here (native query
 * result sets have no enum type) and converted in
 * `NotificationPersistenceMapper`.
 */
interface NotificationProjection {
    val id: Long
    val userId: String
    val name: String
    val description: String
    val status: String
    val category: String
    val priority: String
    val referenceType: String?
    val referenceId: String?
    val actionUrl: String?
    val createdAt: Instant
    val updatedAt: Instant
    val readAt: Instant?
    val dismissedAt: Instant?
}
