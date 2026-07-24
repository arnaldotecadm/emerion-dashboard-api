package br.com.vertice.emerion_dashboard.domain.cognitouser.repository

import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoDirectoryUser

/**
 * Outbound port for reading the live user directory straight from AWS
 * Cognito (Admin API: ListUsers + AdminListGroupsForUser). Implemented in
 * infrastructure/cognito via the AWS SDK - domain/application never see AWS
 * SDK types. Kept separate from `CognitoUserRepository` (the local
 * persistence port) since these are two different systems: one is the
 * external source of truth, the other is our local cache of it.
 */
interface CognitoUserDirectory {
    fun listAllUsers(): List<CognitoDirectoryUser>
}
