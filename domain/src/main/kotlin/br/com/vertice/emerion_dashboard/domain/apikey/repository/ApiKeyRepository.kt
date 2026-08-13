package br.com.vertice.emerion_dashboard.domain.apikey.repository

import br.com.vertice.emerion_dashboard.domain.apikey.model.ApiKey
import java.time.Instant

interface ApiKeyRepository {
    fun findByKeyValue(keyValue: String): ApiKey?
    fun updateLastUsedAt(id: Long, timestamp: Instant)
}
