package br.com.vertice.emerion_dashboard.infrastructure.rest.customerorder.controller

import br.com.vertice.emerion_dashboard.application.customerorder.query.CustomerOrderQueryUseCase
import br.com.vertice.emerion_dashboard.application.customerorder.query.model.ListCustomerOrdersQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.customerorder.mapper.CustomerOrderQueryRestMapper
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerOrdersApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter for the customer order read endpoints consumed by
 * the React frontend. Implements the generated `CustomerOrdersApi`
 * contract, contains no business logic.
 */
@RestController
class CustomerOrderQueryController(
    private val customerOrderQueryUseCase: CustomerOrderQueryUseCase,
) : CustomerOrdersApi {

    override fun getCustomerOrderById(id: Long): ResponseEntity<CustomerOrderResponse> {
        val customerOrder = customerOrderQueryUseCase.getById(id)
        return ResponseEntity.ok(CustomerOrderQueryRestMapper.toResponse(customerOrder))
    }

    override fun listCustomerOrders(
        page: Int,
        size: Int,
        codCli: String?,
        sitres: String?,
        cnpjEmpresa: String?,
    ): ResponseEntity<CustomerOrderPage> {
        val query = ListCustomerOrdersQuery(
            page = page,
            size = size,
            codCli = codCli,
            sitres = sitres,
            cnpjEmpresa = cnpjEmpresa,
        )
        val result = customerOrderQueryUseCase.list(query)
        return ResponseEntity.ok(CustomerOrderQueryRestMapper.toPageResponse(result))
    }
}
