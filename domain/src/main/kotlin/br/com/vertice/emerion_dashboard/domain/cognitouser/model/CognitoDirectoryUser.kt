package br.com.vertice.emerion_dashboard.domain.cognitouser.model

/**
 * A user record as read straight from the AWS Cognito Admin API, before
 * being merged/persisted into the local `cognito_user` cache (see
 * `CognitoUser`). Deliberately has no `id`/timestamps of its own - those are
 * assigned when it's upserted into `CognitoUser`.
 */
data class CognitoDirectoryUser(
    val sub: String,
    val username: String,
    val email: String?,
    val enabled: Boolean,
    val groups: List<String>,
)
