package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.adapter

import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrder
import br.com.vertice.emerion_dashboard.domain.customerorder.repository.CustomerOrderRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.mapper.CustomerOrderPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.repository.CustomerOrderQueryRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.repository.CustomerOrderSpringDataRepository
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.stereotype.Component

/**
 * Adapter implementing the domain's outbound port (`CustomerOrderRepository`).
 * Reads (`findById`, `findAll`) go through `CustomerOrderQueryRepository`'s
 * native-query + projection path (header + line-item rows fetched
 * separately and grouped back together); writes/upserts go through the
 * JPA-entity-backed `CustomerOrderSpringDataRepository`.
 */
@Component
class CustomerOrderRepositoryAdapter(
    private val springDataRepository: CustomerOrderSpringDataRepository,
    private val queryRepository: CustomerOrderQueryRepository,
) : CustomerOrderRepository {

    override fun findById(id: Long): CustomerOrder? {
        val header = queryRepository.findHeaderProjectionById(id) ?: return null
        val items = queryRepository.findItemsByCustomerOrderIds(listOf(id))
        return CustomerOrderPersistenceMapper.toDomain(header, items)
    }

    override fun findByExternalId(externalId: String): CustomerOrder? =
        springDataRepository.findByExternalId(externalId)?.let(CustomerOrderPersistenceMapper::toDomain)

    override fun findAll(
        pageRequest: PageRequest,
        codCli: String?,
        sitres: String?,
        cnpjEmpresa: String?,
    ): Page<CustomerOrder> {
        val springPageable = SpringPageRequest.of(pageRequest.page, pageRequest.size)
        val headerPage = queryRepository.searchHeaders(
            codCli?.takeIf { it.isNotBlank() },
            sitres?.takeIf { it.isNotBlank() },
            cnpjEmpresa?.takeIf { it.isNotBlank() },
            springPageable,
        )
        val headerIds = headerPage.content.map { it.id }
        val itemsByHeaderId = if (headerIds.isEmpty()) {
            emptyMap()
        } else {
            queryRepository.findItemsByCustomerOrderIds(headerIds).groupBy { it.customerOrderId }
        }
        val content = headerPage.content.map { header ->
            CustomerOrderPersistenceMapper.toDomain(header, itemsByHeaderId[header.id].orEmpty())
        }
        return Page(
            content = content,
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = headerPage.totalElements,
        )
    }

    override fun save(order: CustomerOrder): CustomerOrder {
        val existing = springDataRepository.findByExternalId(order.externalId)
        val entity = CustomerOrderPersistenceMapper.toEntity(order, existing)
        val saved = springDataRepository.save(entity)
        return CustomerOrderPersistenceMapper.toDomain(saved)
    }
}
