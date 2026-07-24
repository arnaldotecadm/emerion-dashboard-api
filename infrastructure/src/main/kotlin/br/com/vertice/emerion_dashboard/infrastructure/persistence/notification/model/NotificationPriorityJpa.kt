package br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.model

/** JPA-local mirror of `domain.notification.model.NotificationPriority` — kept separate so a domain enum rename never silently breaks persisted string values. */
enum class NotificationPriorityJpa {
    LOW,
    MEDIUM,
    HIGH,
}
