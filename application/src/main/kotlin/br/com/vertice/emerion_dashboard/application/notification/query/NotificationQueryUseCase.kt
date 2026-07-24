package br.com.vertice.emerion_dashboard.application.notification.query

import br.com.vertice.emerion_dashboard.application.notification.query.model.ListNotificationsQuery
import br.com.vertice.emerion_dashboard.domain.notification.model.Notification
import br.com.vertice.emerion_dashboard.domain.shared.Page

/**
 * Inbound port (driving port) for the notification read side and the two
 * status-transition actions (mark-as-read/dismiss), consumed by the REST
 * query adapter. Grouped on one interface since they all serve the same
 * functional concern (the authenticated user managing their own
 * notifications) — every method is scoped by `userId` so one user can
 * never read or act on another user's notifications.
 */
interface NotificationQueryUseCase {
    fun getById(id: Long, userId: String): Notification
    fun list(query: ListNotificationsQuery): Page<Notification>
    fun markAsRead(id: Long, userId: String): Notification
    fun dismiss(id: Long, userId: String): Notification
}
