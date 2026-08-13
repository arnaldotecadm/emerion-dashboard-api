package br.com.vertice.emerion_dashboard.domain.apikey.exception

class ApiKeyInvalidException(
    override val message: String = "Invalid or disabled API key"
) : RuntimeException(message)
