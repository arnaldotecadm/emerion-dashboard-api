package br.com.vertice.emerion_dashboard.infrastructure.rest.admin.controller

import br.com.vertice.emerion_dashboard.application.cognitouser.sync.SyncCognitoUsersUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.admin.mapper.CognitoUserSyncRestMapper
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.AdminApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CognitoUserSyncResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter for admin-only operational endpoints. Implements
 * the generated `AdminApi` contract, contains no business logic. Access is
 * restricted to the Cognito ADMIN group at the security-filter-chain level
 * (see SecurityConfig), not here.
 */
@RestController
class CognitoUserSyncController(
    private val syncCognitoUsersUseCase: SyncCognitoUsersUseCase,
) : AdminApi {

    override fun syncCognitoUsers(): ResponseEntity<CognitoUserSyncResponse> {
        val result = syncCognitoUsersUseCase.sync()
        return ResponseEntity.ok(CognitoUserSyncRestMapper.toResponse(result))
    }
}
