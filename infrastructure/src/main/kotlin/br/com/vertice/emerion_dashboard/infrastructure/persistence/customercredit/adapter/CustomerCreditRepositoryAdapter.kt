package br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.adapter

import br.com.vertice.emerion_dashboard.domain.customercredit.model.CustomerCredit
import br.com.vertice.emerion_dashboard.domain.customercredit.repository.CustomerCreditRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.mapper.CustomerCreditPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.repository.CustomerCreditQueryRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.repository.CustomerCreditSpringDataRepository
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.stereotype.Component

/**
 * Adapter implementing the domain's outbound port
 * (`CustomerCreditRepository`). Reads go through
 * `CustomerCreditQueryRepository`'s native-query + projection path;
 * writes/upserts go through the JPA-entity-backed
 * `CustomerCreditSpringDataRepository`.
 */
@Component
class CustomerCreditRepositoryAdapter(
    private val springDataRepository: CustomerCreditSpringDataRepository,
    private val queryRepository: CustomerCreditQueryRepository,
) : CustomerCreditRepository {

    override fun findById(id: Long): CustomerCredit? =
        queryRepository.findProjectionById(id)?.let(CustomerCreditPersistenceMapper::toDomain)

    override fun findByCustomerExternalIdAndSequencia(customerExternalId: String, sequencia: String): CustomerCredit? =
        springDataRepository.findByCustomerExternalIdAndSequencia(customerExternalId, sequencia)
            ?.let(CustomerCreditPersistenceMapper::toDomain)

    override fun findAll(
        pageRequest: PageRequest,
        customerExternalId: String?,
        tipo: String?,
    ): Page<CustomerCredit> {
        val springPageable = SpringPageRequest.of(pageRequest.page, pageRequest.size)
        val result = queryRepository.search(
            customerExternalId?.takeIf { it.isNotBlank() },
            tipo?.takeIf { it.isNotBlank() },
            springPageable,
        )
        return Page(
            content = result.content.map(CustomerCreditPersistenceMapper::toDomain),
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = result.totalElements,
        )
    }

    override fun save(credit: CustomerCredit): CustomerCredit {
        val existing = credit.sequencia?.let {
            springDataRepository.findByCustomerExternalIdAndSequencia(credit.customerExternalId, it)
        }
        val entity = CustomerCreditPersistenceMapper.toEntity(credit, existing)
        val saved = springDataRepository.save(entity)
        return CustomerCreditPersistenceMapper.toDomain(saved)
    }
}
