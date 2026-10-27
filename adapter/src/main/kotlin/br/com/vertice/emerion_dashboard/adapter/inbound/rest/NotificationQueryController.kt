package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.NotificationQueryRestMapper.toDomain
import br.com.vertice.emerion_dashboard.adapter.mapper.NotificationQueryRestMapper.toPageResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.NotificationQueryRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.notification.query.NotificationQueryService
import br.com.vertice.emerion_dashboard.application.notification.query.model.ListNotificationsQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.NotificationsApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationCategory
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.RestController

@RestController
class NotificationQueryController(
    private val notificationQueryService: NotificationQueryService,
) : NotificationsApi {

    override fun getNotificationById(id: Long): ResponseEntity<NotificationResponse> {
        val notification = notificationQueryService.getById(id, currentUserId())
        return ResponseEntity.ok(notification.toResponse())
    }

    override fun listNotifications(
        page: Int,
        size: Int,
        status: NotificationStatus?,
        category: NotificationCategory?,
    ): ResponseEntity<NotificationPage> {
        val query = ListNotificationsQuery(
            page = page,
            size = size,
            userId = currentUserId(),
            status = status?.toDomain(),
            category = category?.toDomain(),
        )
        val result = notificationQueryService.list(query)
        return ResponseEntity.ok(result.toPageResponse())
    }

    override fun markNotificationAsRead(id: Long): ResponseEntity<NotificationResponse> {
        val notification = notificationQueryService.markAsRead(id, currentUserId())
        return ResponseEntity.ok(notification.toResponse())
    }

    override fun dismissNotification(id: Long): ResponseEntity<NotificationResponse> {
        val notification = notificationQueryService.dismiss(id, currentUserId())
        return ResponseEntity.ok(notification.toResponse())
    }

    /** Cognito `sub` claim of the caller, taken from the JWT authentication already established by Spring Security. */
    private fun currentUserId(): String {
        val authentication = SecurityContextHolder.getContext().authentication as JwtAuthenticationToken
        return checkNotNull((authentication.principal as Jwt).subject) { "JWT is missing the required 'sub' claim" }
    }
}
