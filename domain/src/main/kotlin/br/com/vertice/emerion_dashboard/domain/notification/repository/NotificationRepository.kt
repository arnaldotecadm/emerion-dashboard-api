package br.com.vertice.emerion_dashboard.domain.notification.repository

import br.com.vertice.emerion_dashboard.domain.notification.model.Notification
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationCategory
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationStatus
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest

/**
 * Outbound port (driven port) for Notification persistence. Implemented by
 * an adapter in infrastructure/persistence/notification. Every read is
 * scoped to a single `userId` (the Cognito `sub`) so one user can never
 * read or act on another user's notifications.
 */
interface NotificationRepository {
    fun findByIdAndUserId(id: Long, userId: String): Notification?

    fun findAll(
        pageRequest: PageRequest,
        userId: String,
        status: NotificationStatus?,
        category: NotificationCategory?,
    ): Page<Notification>

    /** Insert (new notification, `id == null`) or update (existing) and return the persisted notification. */
    fun save(notification: Notification): Notification
}
