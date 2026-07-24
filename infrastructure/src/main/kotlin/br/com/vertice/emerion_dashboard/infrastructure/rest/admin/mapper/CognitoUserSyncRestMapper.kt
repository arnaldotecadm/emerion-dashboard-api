package br.com.vertice.emerion_dashboard.infrastructure.rest.admin.mapper

import br.com.vertice.emerion_dashboard.application.cognitouser.sync.model.SyncCognitoUsersResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CognitoUserSyncResponse
import java.time.ZoneOffset

/** Maps between the application layer's sync result and the generated OpenAPI response DTO. */
object CognitoUserSyncRestMapper {

    fun toResponse(result: SyncCognitoUsersResult): CognitoUserSyncResponse =
        CognitoUserSyncResponse(
            totalFetched = result.totalFetched,
            totalSynced = result.totalSynced,
            totalFailed = result.totalFailed,
            syncedAt = result.syncedAt.atOffset(ZoneOffset.UTC),
        )
}
