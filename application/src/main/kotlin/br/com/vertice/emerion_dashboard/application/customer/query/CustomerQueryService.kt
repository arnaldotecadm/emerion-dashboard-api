package br.com.vertice.emerion_dashboard.application.customer.query

import br.com.vertice.emerion_dashboard.application.customer.query.model.ListCustomersQuery
import br.com.vertice.emerion_dashboard.domain.customer.exception.CustomerNotFoundException
import br.com.vertice.emerion_dashboard.domain.customer.model.Customer
import br.com.vertice.emerion_dashboard.domain.customer.repository.CustomerRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerQueryService(
    private val customerRepository: CustomerRepository,
) : CustomerQueryUseCase {

    @Transactional(readOnly = true)
    override fun getById(id: Long): Customer =
        customerRepository.findById(id) ?: throw CustomerNotFoundException(id)

    @Transactional(readOnly = true)
    override fun getByExternalId(externalId: String): Customer =
        customerRepository.findByExternalId(externalId) ?: throw CustomerNotFoundException(externalId)

    @Transactional(readOnly = true)
    override fun list(query: ListCustomersQuery): Page<Customer> =
        customerRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            bloqueado = query.bloqueado,
            nomeFantasiaContains = query.nomeFantasiaContains,
        )
}
