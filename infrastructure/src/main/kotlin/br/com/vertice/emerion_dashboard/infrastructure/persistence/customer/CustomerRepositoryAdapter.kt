package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer

import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.domain.customer.CustomerRepository
import br.com.vertice.emerion_dashboard.domain.customer.CustomerStatus
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.stereotype.Component

/**
 * Adapter implementing the domain's outbound port (`CustomerRepository`) on
 * top of Spring Data JPA. This is the only class allowed to depend on both
 * the domain model and the JPA entity.
 */
@Component
class CustomerRepositoryAdapter(
    private val springDataRepository: CustomerSpringDataRepository,
) : CustomerRepository {

    override fun findById(id: Long): Customer? =
        springDataRepository.findById(id).map(CustomerPersistenceMapper::toDomain).orElse(null)

    override fun findByExternalId(externalId: String): Customer? =
        springDataRepository.findByExternalId(externalId)?.let(CustomerPersistenceMapper::toDomain)

    override fun findAll(
        pageRequest: PageRequest,
        status: CustomerStatus?,
        nameContains: String?,
    ): Page<Customer> {
        val jpaStatus = status?.let(CustomerPersistenceMapper::toJpaStatus)
        val springPageable = SpringPageRequest.of(pageRequest.page, pageRequest.size)
        val result = springDataRepository.search(jpaStatus, nameContains?.takeIf { it.isNotBlank() }, springPageable)
        return Page(
            content = result.content.map(CustomerPersistenceMapper::toDomain),
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = result.totalElements,
        )
    }

    override fun save(customer: Customer): Customer {
        val existing = customer.id?.let { springDataRepository.findById(it).orElse(null) }
            ?: customer.externalId.let { springDataRepository.findByExternalId(it) }
        val entity = CustomerPersistenceMapper.toEntity(customer, existing)
        val saved = springDataRepository.save(entity)
        return CustomerPersistenceMapper.toDomain(saved)
    }
}
