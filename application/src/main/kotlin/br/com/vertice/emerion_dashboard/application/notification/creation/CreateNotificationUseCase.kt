package br.com.vertice.emerion_dashboard.application.notification.creation

import br.com.vertice.emerion_dashboard.application.notification.creation.model.CreateNotificationCommand
import br.com.vertice.emerion_dashboard.domain.notification.model.Notification

/**
 * Inbound port for creating notifications, meant to be called by other
 * application services (not by a controller) whenever this API's own
 * business logic determines a user should be notified of something.
 */
interface CreateNotificationUseCase {
    fun create(command: CreateNotificationCommand): Notification
}
