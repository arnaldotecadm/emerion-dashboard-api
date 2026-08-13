package br.com.vertice.emerion_dashboard.infrastructure.persistence.apikey.mapper

import br.com.vertice.emerion_dashboard.domain.apikey.model.ApiKey
import br.com.vertice.emerion_dashboard.infrastructure.persistence.apikey.model.ApiKeyJpaEntity

object ApiKeyPersistenceMapper {
    fun toDomain(entity: ApiKeyJpaEntity): ApiKey = ApiKey(
        id = entity.id,
        keyValue = entity.keyValue,
        serverName = entity.serverName,
        enabled = entity.enabled,
        description = entity.description,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        lastUsedAt = entity.lastUsedAt
    )
}
