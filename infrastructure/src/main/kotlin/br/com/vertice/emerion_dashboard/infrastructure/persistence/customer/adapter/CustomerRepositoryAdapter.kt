package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.adapter

import br.com.vertice.emerion_dashboard.domain.customer.model.Customer
import br.com.vertice.emerion_dashboard.domain.customer.repository.CustomerRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.mapper.CustomerPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.repository.CustomerQueryRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.repository.CustomerSpringDataRepository
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.stereotype.Component

/**
 * Adapter implementing the domain's outbound port (`CustomerRepository`).
 * Reads (`findById`, `findAll`) go through `CustomerQueryRepository`'s
 * native-query + projection path; writes/upserts (`save`, and the
 * `findByExternalId`/`findById` lookups needed to preserve the surrogate
 * key across an update) go through the JPA-entity-backed
 * `CustomerSpringDataRepository`. This is the only class allowed to depend
 * on both the domain model and the persistence types (entity/projection).
 */
@Component
class CustomerRepositoryAdapter(
    private val springDataRepository: CustomerSpringDataRepository,
    private val queryRepository: CustomerQueryRepository,
) : CustomerRepository {

    override fun findById(id: Long): Customer? =
        queryRepository.findProjectionById(id)?.let(CustomerPersistenceMapper::toDomain)

    override fun findByExternalId(externalId: String): Customer? =
        springDataRepository.findByExternalId(externalId)?.let(CustomerPersistenceMapper::toDomain)

    override fun findAll(
        pageRequest: PageRequest,
        bloqueado: Boolean?,
        nomeFantasiaContains: String?,
        cnpjEmpresa: String?,
    ): Page<Customer> {
        val springPageable = SpringPageRequest.of(pageRequest.page, pageRequest.size)
        val result = queryRepository.search(
            bloqueado,
            nomeFantasiaContains?.takeIf { it.isNotBlank() },
            cnpjEmpresa?.takeIf { it.isNotBlank() },
            springPageable,
        )
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
