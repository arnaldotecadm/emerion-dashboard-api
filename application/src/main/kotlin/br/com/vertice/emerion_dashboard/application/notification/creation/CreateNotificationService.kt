package br.com.vertice.emerion_dashboard.application.notification.creation

import br.com.vertice.emerion_dashboard.application.notification.creation.model.CreateNotificationCommand
import br.com.vertice.emerion_dashboard.domain.notification.model.Notification
import br.com.vertice.emerion_dashboard.domain.notification.repository.NotificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class CreateNotificationService(
    private val notificationRepository: NotificationRepository,
    private val clock: Clock = Clock.systemUTC(),
) : CreateNotificationUseCase {

    @Transactional
    override fun create(command: CreateNotificationCommand): Notification {
        val notification = Notification.create(
            userId = command.userId,
            name = command.name,
            description = command.description,
            category = command.category,
            priority = command.priority,
            referenceType = command.referenceType,
            referenceId = command.referenceId,
            actionUrl = command.actionUrl,
            now = Instant.now(clock),
        )
        return notificationRepository.save(notification)
    }
}
