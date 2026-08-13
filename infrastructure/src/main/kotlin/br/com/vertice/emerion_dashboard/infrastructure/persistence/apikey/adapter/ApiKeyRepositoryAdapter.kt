package br.com.vertice.emerion_dashboard.infrastructure.persistence.apikey.adapter

import br.com.vertice.emerion_dashboard.domain.apikey.model.ApiKey
import br.com.vertice.emerion_dashboard.domain.apikey.repository.ApiKeyRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.apikey.mapper.ApiKeyPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.apikey.repository.ApiKeySpringDataRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class ApiKeyRepositoryAdapter(
    private val repository: ApiKeySpringDataRepository
) : ApiKeyRepository {
    
    override fun findByKeyValue(keyValue: String): ApiKey? {
        return repository.findByKeyValueAndEnabledTrue(keyValue)?.let(ApiKeyPersistenceMapper::toDomain)
    }
    
    @Transactional
    override fun updateLastUsedAt(id: Long, timestamp: Instant) {
        repository.updateLastUsedAt(id, timestamp)
    }
}
