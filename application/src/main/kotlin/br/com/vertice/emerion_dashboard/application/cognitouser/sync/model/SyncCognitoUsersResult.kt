package br.com.vertice.emerion_dashboard.application.cognitouser.sync.model

import java.time.Instant

/** Outcome of a Cognito user directory sync (startup or manual admin-triggered refresh). */
data class SyncCognitoUsersResult(
    val totalFetched: Int,
    val totalSynced: Int,
    val totalFailed: Int,
    val syncedAt: Instant,
)
