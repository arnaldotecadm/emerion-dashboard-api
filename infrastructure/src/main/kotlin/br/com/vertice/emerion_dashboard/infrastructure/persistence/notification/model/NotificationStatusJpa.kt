package br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.model

/** JPA-local mirror of `domain.notification.model.NotificationStatus` — kept separate so a domain enum rename never silently breaks persisted string values. */
enum class NotificationStatusJpa {
    UNREAD,
    READ,
    DISMISSED,
}
