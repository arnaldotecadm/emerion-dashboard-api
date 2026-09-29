package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.customer.CustomerService
import br.com.vertice.emerion_dashboard.domain.customer.ListCustomersQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomersApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerController(
    private val customerService: CustomerService,
) : CustomersApi {

    override fun getCustomerById(id: Long): ResponseEntity<CustomerResponse> {
        val customer = customerService.getById(id)
        return ResponseEntity.ok(customer.toResponse())
    }

    override fun getCustomerByExternalId(externalId: String): ResponseEntity<CustomerResponse> {
        val customer = customerService.getByExternalId(externalId)
        return ResponseEntity.ok(customer.toResponse())
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
        val result = customerService.list(query)
        return ResponseEntity.ok(
            CustomerPage(
                data = result.content.map { it.toResponse() },
                pagination = PaginationInfo(
                    total = result.totalElements,
                    page = result.page,
                    propertySize = result.size,
                    totalPages = result.totalPages,
                ),
            ),
        )
    }
}
