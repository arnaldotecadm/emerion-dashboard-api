package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.CognitoUserJpaEntity
import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoUser

/** Maps between the domain model and the JPA entity. Kept out of the entity/domain classes on purpose. */
object CognitoUserPersistenceMapper {

    fun CognitoUserJpaEntity.toDomain(): CognitoUser =
        CognitoUser(
            id = this.id,
            sub = this.sub,
            username = this.username,
            email = this.email,
            enabled = this.enabled,
            cnpjEmpresa = this.cnpjEmpresa,
            groups = this.groups.toList(),
            lastSyncedAt = this.lastSyncedAt,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun CognitoUser.toEntity(existing: CognitoUserJpaEntity?): CognitoUserJpaEntity =
        CognitoUserJpaEntity(
            id = existing?.id ?: this.id,
            sub = this.sub,
            username = this.username,
            email = this.email,
            enabled = this.enabled,
            cnpjEmpresa = this.cnpjEmpresa,
            groups = this.groups.toMutableList(),
            lastSyncedAt = this.lastSyncedAt,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
        )
}
