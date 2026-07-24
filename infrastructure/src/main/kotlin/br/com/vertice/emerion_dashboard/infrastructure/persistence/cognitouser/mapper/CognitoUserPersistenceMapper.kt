package br.com.vertice.emerion_dashboard.infrastructure.persistence.cognitouser.mapper

import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoUser
import br.com.vertice.emerion_dashboard.infrastructure.persistence.cognitouser.model.CognitoUserJpaEntity

/** Maps between the domain model and the JPA entity. Kept out of the entity/domain classes on purpose. */
object CognitoUserPersistenceMapper {

    fun toDomain(entity: CognitoUserJpaEntity): CognitoUser =
        CognitoUser(
            id = entity.id,
            sub = entity.sub,
            username = entity.username,
            email = entity.email,
            enabled = entity.enabled,
            groups = entity.groups.toList(),
            lastSyncedAt = entity.lastSyncedAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun toEntity(domain: CognitoUser, existing: CognitoUserJpaEntity?): CognitoUserJpaEntity =
        CognitoUserJpaEntity(
            id = existing?.id ?: domain.id,
            sub = domain.sub,
            username = domain.username,
            email = domain.email,
            enabled = domain.enabled,
            groups = domain.groups.toMutableList(),
            lastSyncedAt = domain.lastSyncedAt,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )
}
