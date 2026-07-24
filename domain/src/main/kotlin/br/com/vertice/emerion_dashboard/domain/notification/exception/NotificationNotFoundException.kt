package br.com.vertice.emerion_dashboard.domain.notification.exception

class NotificationNotFoundException : RuntimeException {
    constructor(id: Long) : super("Notification with id $id not found")
    constructor(id: Long, userId: String) : super("Notification with id $id not found for user $userId")
}
