package br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.adapter

import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddress
import br.com.vertice.emerion_dashboard.domain.customeraddress.repository.CustomerAddressRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.mapper.CustomerAddressPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.repository.CustomerAddressQueryRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.repository.CustomerAddressSpringDataRepository
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.stereotype.Component

/**
 * Adapter implementing the domain's outbound port
 * (`CustomerAddressRepository`). Reads (`findById`, `findAll`) go through
 * `CustomerAddressQueryRepository`'s native-query + projection path
 * (header + detail rows fetched separately and grouped back together);
 * writes/upserts go through the JPA-entity-backed
 * `CustomerAddressSpringDataRepository`.
 */
@Component
class CustomerAddressRepositoryAdapter(
    private val springDataRepository: CustomerAddressSpringDataRepository,
    private val queryRepository: CustomerAddressQueryRepository,
) : CustomerAddressRepository {

    override fun findById(id: Long): CustomerAddress? {
        val header = queryRepository.findHeaderProjectionById(id) ?: return null
        val details = queryRepository.findDetailsByCustomerAddressIds(listOf(id))
        return CustomerAddressPersistenceMapper.toDomain(header, details)
    }

    override fun findByExternalId(externalId: String): CustomerAddress? =
        springDataRepository.findByExternalId(externalId)?.let(CustomerAddressPersistenceMapper::toDomain)

    override fun findAll(
        pageRequest: PageRequest,
        cpfCnpjContains: String?,
        cnpjEmpresa: String?,
    ): Page<CustomerAddress> {
        val springPageable = SpringPageRequest.of(pageRequest.page, pageRequest.size)
        val headerPage = queryRepository.searchHeaders(
            cpfCnpjContains?.takeIf { it.isNotBlank() },
            cnpjEmpresa?.takeIf { it.isNotBlank() },
            springPageable,
        )
        val headerIds = headerPage.content.map { it.id }
        val detailsByHeaderId = if (headerIds.isEmpty()) {
            emptyMap()
        } else {
            queryRepository.findDetailsByCustomerAddressIds(headerIds).groupBy { it.customerAddressId }
        }
        val content = headerPage.content.map { header ->
            CustomerAddressPersistenceMapper.toDomain(header, detailsByHeaderId[header.id].orEmpty())
        }
        return Page(
            content = content,
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = headerPage.totalElements,
        )
    }

    override fun save(address: CustomerAddress): CustomerAddress {
        val existing = springDataRepository.findByExternalId(address.externalId)
        val entity = CustomerAddressPersistenceMapper.toEntity(address, existing)
        val saved = springDataRepository.save(entity)
        return CustomerAddressPersistenceMapper.toDomain(saved)
    }
}
