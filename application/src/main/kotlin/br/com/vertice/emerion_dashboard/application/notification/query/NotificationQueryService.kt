package br.com.vertice.emerion_dashboard.application.notification.query

import br.com.vertice.emerion_dashboard.application.notification.query.model.ListNotificationsQuery
import br.com.vertice.emerion_dashboard.domain.notification.exception.NotificationNotFoundException
import br.com.vertice.emerion_dashboard.domain.notification.model.Notification
import br.com.vertice.emerion_dashboard.domain.notification.repository.NotificationRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class NotificationQueryService(
    private val notificationRepository: NotificationRepository,
    private val clock: Clock = Clock.systemUTC(),
) : NotificationQueryUseCase {

    @Transactional(readOnly = true)
    override fun getById(id: Long, userId: String): Notification =
        notificationRepository.findByIdAndUserId(id, userId) ?: throw NotificationNotFoundException(id, userId)

    @Transactional(readOnly = true)
    override fun list(query: ListNotificationsQuery): Page<Notification> =
        notificationRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            userId = query.userId,
            status = query.status,
            category = query.category,
        )

    @Transactional
    override fun markAsRead(id: Long, userId: String): Notification {
        val notification = getById(id, userId)
        return notificationRepository.save(notification.markAsRead(Instant.now(clock)))
    }

    @Transactional
    override fun dismiss(id: Long, userId: String): Notification {
        val notification = getById(id, userId)
        return notificationRepository.save(notification.dismiss(Instant.now(clock)))
    }
}
