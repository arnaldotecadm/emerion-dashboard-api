package br.com.vertice.emerion_dashboard.infrastructure.config

import br.com.vertice.emerion_dashboard.domain.apikey.repository.ApiKeyRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant

@Component
class ApiKeyAuthenticationFilter(
    private val apiKeyRepository: ApiKeyRepository
) : OncePerRequestFilter() {
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestPath = request.requestURI
        
        if (requestPath.contains("/ingestion/")) {
            val apiKey = request.getHeader("X-API-Key")
            
            if (apiKey.isNullOrBlank()) {
                respondUnauthorized(response, "Missing API key header")
                return
            }
            
            val validKey = apiKeyRepository.findByKeyValue(apiKey)
            if (validKey == null) {
                respondUnauthorized(response, "Invalid API key")
                return
            }
            
            // Update last_used_at for audit trail
            try {
                apiKeyRepository.updateLastUsedAt(validKey.id, Instant.now())
            } catch (e: Exception) {
                logger.warn("Failed to update last_used_at for API key ${validKey.serverName}", e)
                // Don't fail the request, just log it
            }
        }
        
        filterChain.doFilter(request, response)
    }
    
    private fun respondUnauthorized(response: HttpServletResponse, message: String) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = "application/json"
        response.writer.write("""{"error":{"code":"UNAUTHORIZED","message":"$message"}}""")
    }
}
