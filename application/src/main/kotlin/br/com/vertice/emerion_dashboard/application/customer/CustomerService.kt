package br.com.vertice.emerion_dashboard.application.customer

import br.com.vertice.emerion_dashboard.domain.customer.ListCustomersQuery
import br.com.vertice.emerion_dashboard.domain.exception.CustomerNotFoundException
import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.application.outbound.port.CustomerRepositoryPort
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerService(
    private val customerRepository: CustomerRepositoryPort,
) {

    @Transactional(readOnly = true)
    fun getById(id: Long): Customer =
        customerRepository.findById(id) ?: throw CustomerNotFoundException(id)

    @Transactional(readOnly = true)
    fun getByExternalId(externalId: String): Customer =
        customerRepository.findByExternalId(externalId) ?: throw CustomerNotFoundException(externalId)

    @Transactional(readOnly = true)
    fun list(query: ListCustomersQuery): Page<Customer> =
        customerRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            bloqueado = query.bloqueado,
            nomeFantasiaContains = query.nomeFantasiaContains,
            cnpjEmpresa = query.cnpjEmpresa,
        )
}
