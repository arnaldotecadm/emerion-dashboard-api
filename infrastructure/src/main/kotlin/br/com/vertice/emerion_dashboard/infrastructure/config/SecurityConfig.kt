package br.com.vertice.emerion_dashboard.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

/**
 * Only the query/read endpoints (React-facing, e.g. `/customers`,
 * `/products`, ...) require a valid Cognito JWT. The ingestion endpoints
 * (everything under the "ingestion" path prefix, called server-to-server by
 * emerion-load-service) and the OpenAPI/Swagger static resources stay open -
 * see CognitoJwtConfig for how the token itself is validated. Authenticated
 * callers must additionally belong to the Cognito group configured in
 * `app.security.cognito.required-group`, except everything under the admin
 * path prefix, which requires the separate, stricter
 * `app.security.cognito.admin-group` instead (so regular COMPANY users
 * can't hit admin-only operations like the Cognito user sync trigger).
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationConverter: JwtAuthenticationConverter,
    @Value("\${app.security.cognito.required-group}") private val requiredGroup: String,
    @Value("\${app.security.cognito.admin-group}") private val adminGroup: String,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeHttpRequests {
                // CORS preflight requests carry no Authorization header and must be
                // let through, or the browser never gets to send the real request.
                authorize(HttpMethod.OPTIONS, "/**", permitAll)
                authorize("/ingestion/**", permitAll)
                authorize("/openapi/**", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/swagger-ui.html", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/admin/**", hasAuthority("ROLE_$adminGroup"))
                authorize(anyRequest, hasAuthority("ROLE_$requiredGroup"))
            }
            oauth2ResourceServer {
                jwt {
                    jwtAuthenticationConverter = this@SecurityConfig.jwtAuthenticationConverter
                }
            }
        }
        return http.build()
    }
}
