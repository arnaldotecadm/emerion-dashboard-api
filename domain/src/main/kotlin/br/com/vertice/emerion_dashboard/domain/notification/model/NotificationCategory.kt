package br.com.vertice.emerion_dashboard.domain.notification.model

/** Fixed set of notification categories — extend as new triggers are added. */
enum class NotificationCategory {
    INGESTION,
    STATUS_UPDATE,
    APPROVAL_NEEDED,
    SYSTEM,
}
