package br.com.vertice.emerion_dashboard.infrastructure.rest.customer

import br.com.vertice.emerion_dashboard.application.customer.GetCustomerUseCase
import br.com.vertice.emerion_dashboard.application.customer.ListCustomersQuery
import br.com.vertice.emerion_dashboard.application.customer.ListCustomersUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomersApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter for the customer read endpoints consumed by the
 * React frontend. Implements the generated `CustomersApi` contract, contains
 * no business logic.
 */
@RestController
class CustomerQueryController(
    private val getCustomerUseCase: GetCustomerUseCase,
    private val listCustomersUseCase: ListCustomersUseCase,
) : CustomersApi {

    override fun getCustomerById(id: Long): ResponseEntity<CustomerResponse> {
        val customer = getCustomerUseCase.getById(id)
        return ResponseEntity.ok(CustomerQueryRestMapper.toResponse(customer))
    }

    override fun listCustomers(
        page: Int,
        size: Int,
        status: CustomerStatus?,
        name: String?,
    ): ResponseEntity<CustomerPage> {
        val query = ListCustomersQuery(
            page = page,
            size = size,
            status = CustomerQueryRestMapper.toDomainStatus(status),
            nameContains = name,
        )
        val result = listCustomersUseCase.list(query)
        return ResponseEntity.ok(CustomerQueryRestMapper.toPageResponse(result))
    }
}
