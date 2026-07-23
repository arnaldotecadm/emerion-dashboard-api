package br.com.vertice.emerion_dashboard.infrastructure.rest.customer.controller

import br.com.vertice.emerion_dashboard.application.customer.query.CustomerQueryUseCase
import br.com.vertice.emerion_dashboard.application.customer.query.model.ListCustomersQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.customer.mapper.CustomerQueryRestMapper
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomersApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter for the customer read endpoints consumed by the
 * React frontend. Implements the generated `CustomersApi` contract, contains
 * no business logic.
 */
@RestController
class CustomerQueryController(
    private val customerQueryUseCase: CustomerQueryUseCase,
) : CustomersApi {

    override fun getCustomerById(id: Long): ResponseEntity<CustomerResponse> {
        val customer = customerQueryUseCase.getById(id)
        return ResponseEntity.ok(CustomerQueryRestMapper.toResponse(customer))
    }

    override fun getCustomerByExternalId(externalId: String): ResponseEntity<CustomerResponse> {
        val customer = customerQueryUseCase.getByExternalId(externalId)
        return ResponseEntity.ok(CustomerQueryRestMapper.toResponse(customer))
    }

    override fun listCustomers(
        page: Int,
        size: Int,
        bloqueado: Boolean?,
        nomeFantasia: String?,
        cnpjEmpresa: String?,
    ): ResponseEntity<CustomerPage> {
        val query = ListCustomersQuery(
            page = page,
            size = size,
            bloqueado = bloqueado,
            nomeFantasiaContains = nomeFantasia,
            cnpjEmpresa = cnpjEmpresa,
        )
        val result = customerQueryUseCase.list(query)
        return ResponseEntity.ok(CustomerQueryRestMapper.toPageResponse(result))
    }
}
