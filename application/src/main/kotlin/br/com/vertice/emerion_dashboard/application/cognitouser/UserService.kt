package br.com.vertice.emerion_dashboard.application.cognitouser

import br.com.vertice.emerion_dashboard.application.outbound.port.CognitoUserRepository
import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoUser
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val cognitoUserRepository: CognitoUserRepository,
) {

    @Volatile
    private var cachedCurrentUser: CognitoUser? = null

    fun getCurrentUser(): CognitoUser? =
        cachedCurrentUser ?: synchronized(this) {
            cachedCurrentUser ?: fetchCurrentUser().also { cachedCurrentUser = it }
        }


    private fun fetchCurrentUser(): CognitoUser {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw InternalAuthenticationServiceException("No authentication found in security context")

        val cognitoUser = cognitoUserRepository.findBySub(authentication.name)

        return requireNotNull(cognitoUser){
            "Authenticated user with sub '${authentication.name}' not found in the local cache. " +
            "Please ensure that the user exists in the local `cognito_user` table."
        }
    }
}