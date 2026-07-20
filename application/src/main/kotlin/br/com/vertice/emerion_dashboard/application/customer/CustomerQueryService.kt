package br.com.vertice.emerion_dashboard.application.customer

import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.domain.customer.CustomerNotFoundException
import br.com.vertice.emerion_dashboard.domain.customer.CustomerRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class ListCustomersQuery(
    val page: Int,
    val size: Int,
    val bloqueado: Boolean?,
    val nomeFantasiaContains: String?,
)

/** Inbound port for fetching a single customer, consumed by the REST query adapter. */
fun interface GetCustomerUseCase {
    fun getById(id: Long): Customer
}

/** Inbound port for listing/filtering customers, consumed by the REST query adapter. */
fun interface ListCustomersUseCase {
    fun list(query: ListCustomersQuery): Page<Customer>
}

@Service
class CustomerQueryService(
    private val customerRepository: CustomerRepository,
) : GetCustomerUseCase, ListCustomersUseCase {

    @Transactional(readOnly = true)
    override fun getById(id: Long): Customer =
        customerRepository.findById(id) ?: throw CustomerNotFoundException(id)

    @Transactional(readOnly = true)
    override fun list(query: ListCustomersQuery): Page<Customer> =
        customerRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            bloqueado = query.bloqueado,
            nomeFantasiaContains = query.nomeFantasiaContains,
        )
}
