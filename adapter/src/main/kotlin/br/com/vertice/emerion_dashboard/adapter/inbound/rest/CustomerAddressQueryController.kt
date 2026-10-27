package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerAddressQueryRestMapper.toPageResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerAddressQueryRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.customeraddress.query.CustomerAddressQueryService
import br.com.vertice.emerion_dashboard.application.customeraddress.query.model.ListCustomerAddressesQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerAddressesApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter for the customer address read endpoints consumed
 * by the React frontend. Implements the generated `CustomerAddressesApi`
 * contract, contains no business logic.
 */
@RestController
class CustomerAddressQueryController(
    private val customerAddressQueryService: CustomerAddressQueryService,
) : CustomerAddressesApi {

    override fun getCustomerAddressById(id: Long): ResponseEntity<CustomerAddressResponse> =
        ResponseEntity.ok(customerAddressQueryService.getById(id).toResponse())

    override fun listCustomerAddresses(
        page: Int,
        size: Int,
        cpfCnpj: String?,
        cnpjEmpresa: String?,
    ): ResponseEntity<CustomerAddressPage> {
        val query = ListCustomerAddressesQuery(page = page, size = size, cpfCnpjContains = cpfCnpj, cnpjEmpresa = cnpjEmpresa)
        return ResponseEntity.ok(customerAddressQueryService.list(query).toPageResponse())
    }
}
