package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoUser

/**
 * Outbound port (driven port) for the local `cognito_user` cache table.
 * Implemented by an adapter in infrastructure/persistence/cognitouser (JPA).
 */
interface CognitoUserRepository {
    fun findBySub(sub: String): CognitoUser?
    fun findAll(): List<CognitoUser>
    fun save(user: CognitoUser): CognitoUser
}