package br.com.vertice.emerion_dashboard.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

/**
 * Validates the JWT access tokens issued by the React app's AWS Cognito user
 * pool. The JWK set URI is derived from the issuer following Cognito's fixed
 * convention (`<issuer>/.well-known/jwks.json`) instead of using OIDC
 * discovery (`JwtDecoders.fromIssuerLocation`), so building this bean never
 * makes an eager network call at application/test startup - the key set is
 * only fetched (and cached) lazily, the first time a token is actually
 * decoded.
 */
@Configuration
class CognitoJwtConfig(
    @Value("\${app.security.cognito.issuer-uri}") private val issuerUri: String,
    @Value("\${app.security.cognito.client-id}") private val clientId: String,
) {

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val decoder = NimbusJwtDecoder.withJwkSetUri("$issuerUri/.well-known/jwks.json").build()
        decoder.setJwtValidator(
            DelegatingOAuth2TokenValidator(
                JwtValidators.createDefaultWithIssuer(issuerUri),
                cognitoAccessTokenValidator(),
            ),
        )
        return decoder
    }

    /**
     * Cognito access tokens (as opposed to ID tokens) don't carry a standard
     * `aud` claim - the app client is identified by `client_id` instead, and
     * `token_use` distinguishes access tokens from ID tokens. Both are
     * checked so only genuine access tokens issued to our app client are
     * accepted.
     */
    private fun cognitoAccessTokenValidator(): OAuth2TokenValidator<Jwt> =
        OAuth2TokenValidator { jwt ->
            val tokenUse = jwt.claims["token_use"] as? String
            val tokenClientId = jwt.claims["client_id"] as? String
            if (tokenUse == "access" && tokenClientId == clientId) {
                OAuth2TokenValidatorResult.success()
            } else {
                OAuth2TokenValidatorResult.failure(
                    OAuth2Error(
                        "invalid_token",
                        "The token is not a valid Cognito access token for this app client",
                        null,
                    ),
                )
            }
        }

    /** Maps the `cognito:groups` claim onto Spring Security authorities (`ROLE_<group>`). */
    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter { jwt ->
            @Suppress("UNCHECKED_CAST")
            val groups = jwt.claims["cognito:groups"] as? List<String> ?: emptyList()
            groups.map { SimpleGrantedAuthority("ROLE_$it") }
        }
        return converter
    }
}
