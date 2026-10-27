package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerOrderQueryRestMapper.toPageResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerOrderQueryRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.customerorder.query.CustomerOrderQueryService
import br.com.vertice.emerion_dashboard.application.customerorder.query.model.ListCustomerOrdersQuery
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
    private val customerOrderQueryService: CustomerOrderQueryService,
) : CustomerOrdersApi {

    override fun getCustomerOrderById(id: Long): ResponseEntity<CustomerOrderResponse> =
        ResponseEntity.ok(customerOrderQueryService.getById(id).toResponse())

    override fun listCustomerOrders(
        page: Int,
        size: Int,
        codigoCliente: Int?,
        statusPedido: String?,
        codigoEmpresa: Int?,
        nomeFantasia: String?,
        numeroPedido: String?,
        period: String,
    ): ResponseEntity<CustomerOrderPage> {
        val query = ListCustomerOrdersQuery(
            page = page,
            size = size,
            codigoCliente = codigoCliente,
            statusPedido = statusPedido,
            codigoEmpresa = codigoEmpresa,
            nomeFantasia = nomeFantasia,
            numeroPedido = numeroPedido,
            period = period,
        )
        return ResponseEntity.ok(customerOrderQueryService.list(query).toPageResponse())
    }
}
