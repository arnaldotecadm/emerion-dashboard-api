package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.mapper

import br.com.vertice.emerion_dashboard.domain.customer.model.Customer
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.model.CustomerJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.projection.CustomerProjection

/** Maps between the domain model and the JPA entity/read projection. Kept out of the entity/domain classes on purpose. */
object CustomerPersistenceMapper {

    /** Read path: native-query projection (see `CustomerQueryRepository`) -> domain model. */
    fun toDomain(projection: CustomerProjection): Customer =
        Customer(
            id = projection.id,
            externalId = projection.externalId,
            nomeFantasia = projection.nomeFantasia,
            razaoSocial = projection.razaoSocial,
            cpfCnpj = projection.cpfCnpj,
            inscricaoEstadual = projection.inscricaoEstadual,
            regimeTributario = projection.regimeTributario,
            bloqueado = projection.bloqueado,
            createdAt = projection.createdAt,
            updatedAt = projection.updatedAt,
        )

    /** Write path: JPA entity -> domain model. */
    fun toDomain(entity: CustomerJpaEntity): Customer =
        Customer(
            id = entity.id,
            externalId = entity.externalId,
            nomeFantasia = entity.nomeFantasia,
            razaoSocial = entity.razaoSocial,
            cpfCnpj = entity.cpfCnpj,
            inscricaoEstadual = entity.inscricaoEstadual,
            regimeTributario = entity.regimeTributario,
            bloqueado = entity.bloqueado,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun toEntity(domain: Customer, existing: CustomerJpaEntity?): CustomerJpaEntity =
        CustomerJpaEntity(
            id = existing?.id ?: domain.id,
            externalId = domain.externalId,
            nomeFantasia = domain.nomeFantasia,
            razaoSocial = domain.razaoSocial,
            cpfCnpj = domain.cpfCnpj,
            inscricaoEstadual = domain.inscricaoEstadual,
            regimeTributario = domain.regimeTributario,
            bloqueado = domain.bloqueado,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )
}
