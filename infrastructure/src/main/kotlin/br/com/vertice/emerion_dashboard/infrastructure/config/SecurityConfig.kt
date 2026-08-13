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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Security configuration:
 * - Ingestion endpoints: require valid API key (via ApiKeyAuthenticationFilter)
 * - Query/read endpoints: require valid Cognito JWT + ROLE_COMPANY
 * - Admin endpoints: require Cognito JWT + ROLE_ADMIN
 * - OpenAPI/Swagger: publicly accessible
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationConverter: JwtAuthenticationConverter,
    private val apiKeyAuthenticationFilter: ApiKeyAuthenticationFilter,
    @Value("\${app.security.cognito.required-group}") private val requiredGroup: String,
    @Value("\${app.security.cognito.admin-group}") private val adminGroup: String,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            
            // Add API key filter before JWT filter
            addFilterBefore<UsernamePasswordAuthenticationFilter>(apiKeyAuthenticationFilter)
            
            authorizeHttpRequests {
                // CORS preflight requests carry no Authorization header and must be
                // let through, or the browser never gets to send the real request.
                authorize(HttpMethod.OPTIONS, "/**", permitAll)
                authorize("/ingestion/**", permitAll)  // API key filter validates these
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
