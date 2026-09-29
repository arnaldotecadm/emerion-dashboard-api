package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.port

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.mapper.CustomerPersistenceMapper.toDomain
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.mapper.CustomerPersistenceMapper.toEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository.CustomerRepository
import br.com.vertice.emerion_dashboard.application.outbound.port.CustomerRepositoryPort
import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Component
import org.springframework.data.domain.PageRequest as SpringPageRequest

@Component
class CustomerRepositoryPortAdapter(
    private val customerRepository: CustomerRepository,
) : CustomerRepositoryPort {

    override fun findById(id: Long): Customer? =
        customerRepository.findProjectionById(id)?.toDomain()

    override fun findByExternalId(externalId: String): Customer? =
        customerRepository.findByExternalId(externalId)?.toDomain()

    override fun findAll(
        pageRequest: PageRequest,
        bloqueado: Boolean?,
        nomeFantasiaContains: String?,
        cnpjEmpresa: String?,
    ): Page<Customer> {
        val springPageable = SpringPageRequest.of(pageRequest.page, pageRequest.size)
        val result = customerRepository.search(
            bloqueado,
            nomeFantasiaContains?.takeIf { it.isNotBlank() },
            cnpjEmpresa?.takeIf { it.isNotBlank() },
            springPageable,
        )
        return Page(
            content = result.content.map { it.toDomain() },
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = result.totalElements,
        )
    }

    override fun save(customer: Customer): Customer {
        val existing = customer.id?.let { customerRepository.findById(it).orElse(null) }
            ?: customer.externalId.let { customerRepository.findByExternalId(it) }
        val entity = customer.toEntity(existing)
        val saved = customerRepository.save(entity)
        return saved.toDomain()
    }
}
