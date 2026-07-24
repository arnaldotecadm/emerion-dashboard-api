package br.com.vertice.emerion_dashboard.domain.cognitouser.model

import java.time.Instant

/**
 * Local cache of an AWS Cognito user, kept in sync (at startup and via a
 * manual admin-triggered refresh) so this API knows which users/groups
 * exist and can target notifications at them, without calling Cognito's
 * Admin API on every request. Plain Kotlin, no JPA/AWS SDK types.
 */
data class CognitoUser(
    val id: Long?,
    val sub: String,
    val username: String,
    val email: String?,
    val enabled: Boolean,
    val groups: List<String>,
    val lastSyncedAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        /** Factory for a brand-new local record for a Cognito user not seen before (no id yet). */
        fun fromDirectory(directoryUser: CognitoDirectoryUser, now: Instant) = CognitoUser(
            id = null,
            sub = directoryUser.sub,
            username = directoryUser.username,
            email = directoryUser.email,
            enabled = directoryUser.enabled,
            groups = directoryUser.groups,
            lastSyncedAt = now,
            createdAt = now,
            updatedAt = now,
        )
    }

    /** Applies a fresh directory read on top of an existing local record, bumping updatedAt/lastSyncedAt. */
    fun refreshedFrom(directoryUser: CognitoDirectoryUser, now: Instant) = copy(
        username = directoryUser.username,
        email = directoryUser.email,
        enabled = directoryUser.enabled,
        groups = directoryUser.groups,
        lastSyncedAt = now,
        updatedAt = now,
    )
}
