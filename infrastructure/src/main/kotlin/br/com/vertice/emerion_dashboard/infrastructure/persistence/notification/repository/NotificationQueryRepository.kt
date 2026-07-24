package br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.model.NotificationJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.notification.projection.NotificationProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

/**
 * Read-only side of notification persistence: native SQL mapped straight to
 * `NotificationProjection` (mirrors emerion-load-service's
 * repository/<x>QueryRepository native-query + projection pattern), kept
 * separate from `NotificationSpringDataRepository` (JPA entity, writes
 * only). Every query is scoped by `user_id` so one user can never read
 * another user's notifications.
 */
interface NotificationQueryRepository : Repository<NotificationJpaEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                user_id AS userId,
                name,
                description,
                status,
                category,
                priority,
                reference_type AS referenceType,
                reference_id AS referenceId,
                action_url AS actionUrl,
                created_at AS createdAt,
                updated_at AS updatedAt,
                read_at AS readAt,
                dismissed_at AS dismissedAt
            FROM notification
            WHERE id = :id
              AND user_id = :userId
        """,
    )
    fun findProjectionByIdAndUserId(@Param("id") id: Long, @Param("userId") userId: String): NotificationProjection?

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                user_id AS userId,
                name,
                description,
                status,
                category,
                priority,
                reference_type AS referenceType,
                reference_id AS referenceId,
                action_url AS actionUrl,
                created_at AS createdAt,
                updated_at AS updatedAt,
                read_at AS readAt,
                dismissed_at AS dismissedAt
            FROM notification
            WHERE user_id = :userId
              AND (:status IS NULL OR status = :status)
              AND (:category IS NULL OR category = :category)
        """,
        countQuery = """
            SELECT count(*)
            FROM notification
            WHERE user_id = :userId
              AND (:status IS NULL OR status = :status)
              AND (:category IS NULL OR category = :category)
        """,
    )
    fun search(
        @Param("userId") userId: String,
        @Param("status") status: String?,
        @Param("category") category: String?,
        pageable: Pageable,
    ): Page<NotificationProjection>
}
