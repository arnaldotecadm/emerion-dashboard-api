package br.com.vertice.emerion_dashboard.application.cognitouser.sync

import br.com.vertice.emerion_dashboard.application.cognitouser.sync.model.SyncCognitoUsersResult

/**
 * Inbound port for syncing the local `cognito_user` cache from AWS
 * Cognito's live user directory. Invoked once at application startup (see
 * infrastructure's startup runner) and on-demand via the admin-only
 * `POST /admin/cognito-users/sync` endpoint.
 */
interface SyncCognitoUsersUseCase {
    fun sync(): SyncCognitoUsersResult
}
