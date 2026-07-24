package br.com.vertice.emerion_dashboard.infrastructure.rest.notification.controller

import br.com.vertice.emerion_dashboard.application.notification.query.NotificationQueryUseCase
import br.com.vertice.emerion_dashboard.application.notification.query.model.ListNotificationsQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.NotificationsApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationCategory
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.NotificationStatus
import br.com.vertice.emerion_dashboard.infrastructure.rest.notification.mapper.NotificationQueryRestMapper
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter for the notification endpoints consumed by the
 * React frontend. Implements the generated `NotificationsApi` contract,
 * contains no business logic. Every operation is scoped to the calling
 * user's Cognito subject, read off the already-validated bearer token in
 * the security context (see CognitoJwtConfig/SecurityConfig) rather than a
 * request parameter — the generated interface has no such parameter to
 * override, and a subject read from the request body/query could be
 * spoofed — so a user can never read or act on someone else's
 * notifications.
 */
@RestController
class NotificationQueryController(
    private val notificationQueryUseCase: NotificationQueryUseCase,
) : NotificationsApi {

    override fun getNotificationById(id: Long): ResponseEntity<NotificationResponse> {
        val notification = notificationQueryUseCase.getById(id, currentUserId())
        return ResponseEntity.ok(NotificationQueryRestMapper.toResponse(notification))
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
            status = status?.let(NotificationQueryRestMapper::toDomain),
            category = category?.let(NotificationQueryRestMapper::toDomain),
        )
        val result = notificationQueryUseCase.list(query)
        return ResponseEntity.ok(NotificationQueryRestMapper.toPageResponse(result))
    }

    override fun markNotificationAsRead(id: Long): ResponseEntity<NotificationResponse> {
        val notification = notificationQueryUseCase.markAsRead(id, currentUserId())
        return ResponseEntity.ok(NotificationQueryRestMapper.toResponse(notification))
    }

    override fun dismissNotification(id: Long): ResponseEntity<NotificationResponse> {
        val notification = notificationQueryUseCase.dismiss(id, currentUserId())
        return ResponseEntity.ok(NotificationQueryRestMapper.toResponse(notification))
    }

    /** Cognito `sub` claim of the caller, taken from the JWT authentication already established by Spring Security. */
    private fun currentUserId(): String {
        val authentication = SecurityContextHolder.getContext().authentication as JwtAuthenticationToken
        return checkNotNull((authentication.principal as Jwt).subject) { "JWT is missing the required 'sub' claim" }
    }
}
