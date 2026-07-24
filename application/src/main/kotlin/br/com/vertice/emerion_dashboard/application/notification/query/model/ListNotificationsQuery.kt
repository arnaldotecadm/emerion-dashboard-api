package br.com.vertice.emerion_dashboard.application.notification.query.model

import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationCategory
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationStatus

/** Input query for listing/filtering the authenticated user's notifications (paginated). */
data class ListNotificationsQuery(
    val page: Int,
    val size: Int,
    val userId: String,
    val status: NotificationStatus?,
    val category: NotificationCategory?,
)
