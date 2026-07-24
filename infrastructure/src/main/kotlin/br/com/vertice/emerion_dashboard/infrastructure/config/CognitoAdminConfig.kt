package br.com.vertice.emerion_dashboard.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient

/**
 * AWS Cognito Admin API client, used only to sync the local `cognito_user`
 * cache (see SyncCognitoUsersService/CognitoUserDirectoryAdapter) - this is
 * a separate concern from CognitoJwtConfig, which validates JWTs issued by
 * the same user pool but never calls AWS itself.
 *
 * Uses explicit static credentials from configuration for now (per current
 * ops setup running outside AWS). Once deployed onto AWS infrastructure
 * with a task/instance IAM role, switch the credentials provider below to
 * `DefaultCredentialsProvider.create()` and drop the
 * `app.aws.access-key-id`/`app.aws.secret-access-key` properties entirely -
 * no other code needs to change.
 */
@Configuration
class CognitoAdminConfig(
    @Value("\${app.aws.region}") private val region: String,
    @Value("\${app.aws.access-key-id}") private val accessKeyId: String,
    @Value("\${app.aws.secret-access-key}") private val secretAccessKey: String,
) {

    @Bean
    fun cognitoIdentityProviderClient(): CognitoIdentityProviderClient =
        CognitoIdentityProviderClient.builder()
            .region(Region.of(region))
            .credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)),
            )
            .build()
}
