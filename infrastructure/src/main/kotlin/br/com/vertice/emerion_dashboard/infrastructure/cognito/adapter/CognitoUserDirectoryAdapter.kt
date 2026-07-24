package br.com.vertice.emerion_dashboard.infrastructure.cognito.adapter

import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoDirectoryUser
import br.com.vertice.emerion_dashboard.domain.cognitouser.repository.CognitoUserDirectory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType

/**
 * Adapter implementing the domain's outbound port (`CognitoUserDirectory`)
 * on top of the AWS Cognito Admin API: `ListUsers` (paginated) for the
 * user/attribute list, plus one `AdminListGroupsForUser` call per user to
 * resolve group membership (not returned by `ListUsers` itself). This is
 * the only class allowed to depend on the AWS SDK Cognito types.
 */
@Component
class CognitoUserDirectoryAdapter(
    private val cognitoIdentityProviderClient: CognitoIdentityProviderClient,
    @Value("\${app.security.cognito.user-pool-id}") private val userPoolId: String,
) : CognitoUserDirectory {

    override fun listAllUsers(): List<CognitoDirectoryUser> {
        val request = ListUsersRequest.builder().userPoolId(userPoolId).build()
        return cognitoIdentityProviderClient.listUsersPaginator(request)
            .users()
            .map { toDirectoryUser(it) }
            .toList()
    }

    private fun toDirectoryUser(user: UserType): CognitoDirectoryUser {
        val attributes = user.attributes().associate { it.name() to it.value() }
        return CognitoDirectoryUser(
            sub = attributes["sub"] ?: user.username(),
            username = user.username(),
            email = attributes["email"],
            enabled = user.enabled(),
            groups = groupsFor(user.username()),
        )
    }

    private fun groupsFor(username: String): List<String> {
        val request = AdminListGroupsForUserRequest.builder()
            .userPoolId(userPoolId)
            .username(username)
            .build()
        return cognitoIdentityProviderClient.adminListGroupsForUser(request)
            .groups()
            .map { it.groupName() }
    }
}
