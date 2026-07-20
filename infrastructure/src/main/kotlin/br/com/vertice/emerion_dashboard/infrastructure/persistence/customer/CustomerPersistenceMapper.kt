package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer

import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.domain.customer.CustomerStatus

/** Maps between the domain model and the JPA entity. Kept out of the entity/domain classes on purpose. */
object CustomerPersistenceMapper {

    fun toDomain(entity: CustomerJpaEntity): Customer =
        Customer(
            id = entity.id,
            externalId = entity.externalId,
            name = entity.name,
            email = entity.email,
            status = toDomainStatus(entity.status),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun toEntity(domain: Customer, existing: CustomerJpaEntity?): CustomerJpaEntity =
        CustomerJpaEntity(
            id = existing?.id ?: domain.id,
            externalId = domain.externalId,
            name = domain.name,
            email = domain.email,
            status = toJpaStatus(domain.status),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )

    private fun toDomainStatus(status: CustomerStatusJpa): CustomerStatus =
        when (status) {
            CustomerStatusJpa.ACTIVE -> CustomerStatus.ACTIVE
            CustomerStatusJpa.INACTIVE -> CustomerStatus.INACTIVE
            CustomerStatusJpa.UNKNOWN -> CustomerStatus.UNKNOWN
        }

    fun toJpaStatus(status: CustomerStatus): CustomerStatusJpa =
        when (status) {
            CustomerStatus.ACTIVE -> CustomerStatusJpa.ACTIVE
            CustomerStatus.INACTIVE -> CustomerStatusJpa.INACTIVE
            CustomerStatus.UNKNOWN -> CustomerStatusJpa.UNKNOWN
        }
}
