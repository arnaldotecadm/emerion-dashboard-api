package br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.model.NotificationJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Write-only side of notification persistence (create + status updates).
 * Reads go through `NotificationQueryRepository`'s native-query + projection
 * path instead — this interface exists purely so `save`/`findById` have a
 * plain JPA entity to work with.
 */
interface NotificationSpringDataRepository : JpaRepository<NotificationJpaEntity, Long>
