package br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.adapter

import br.com.vertice.emerion_dashboard.domain.notification.model.Notification
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationCategory
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationStatus
import br.com.vertice.emerion_dashboard.domain.notification.repository.NotificationRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.mapper.NotificationPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.repository.NotificationQueryRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.repository.NotificationSpringDataRepository
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.stereotype.Component

/**
 * Adapter implementing the domain's outbound port (`NotificationRepository`).
 * Reads (`findByIdAndUserId`, `findAll`) go through
 * `NotificationQueryRepository`'s native-query + projection path; writes
 * (`save`) go through the JPA-entity-backed
 * `NotificationSpringDataRepository`. This is the only class allowed to
 * depend on both the domain model and the persistence types
 * (entity/projection).
 */
@Component
class NotificationRepositoryAdapter(
    private val springDataRepository: NotificationSpringDataRepository,
    private val queryRepository: NotificationQueryRepository,
) : NotificationRepository {

    override fun findByIdAndUserId(id: Long, userId: String): Notification? =
        queryRepository.findProjectionByIdAndUserId(id, userId)?.let(NotificationPersistenceMapper::toDomain)

    override fun findAll(
        pageRequest: PageRequest,
        userId: String,
        status: NotificationStatus?,
        category: NotificationCategory?,
    ): Page<Notification> {
        val springPageable = SpringPageRequest.of(pageRequest.page, pageRequest.size)
        val result = queryRepository.search(
            userId,
            status?.name,
            category?.name,
            springPageable,
        )
        return Page(
            content = result.content.map(NotificationPersistenceMapper::toDomain),
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = result.totalElements,
        )
    }

    override fun save(notification: Notification): Notification {
        val existing = notification.id?.let { springDataRepository.findById(it).orElse(null) }
        val entity = NotificationPersistenceMapper.toEntity(notification, existing)
        val saved = springDataRepository.save(entity)
        return NotificationPersistenceMapper.toDomain(saved)
    }
}
