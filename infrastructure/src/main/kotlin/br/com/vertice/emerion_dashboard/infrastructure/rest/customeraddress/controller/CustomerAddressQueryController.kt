package br.com.vertice.emerion_dashboard.infrastructure.rest.customeraddress.controller

import br.com.vertice.emerion_dashboard.application.customeraddress.query.CustomerAddressQueryUseCase
import br.com.vertice.emerion_dashboard.application.customeraddress.query.model.ListCustomerAddressesQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.customeraddress.mapper.CustomerAddressQueryRestMapper
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
    private val customerAddressQueryUseCase: CustomerAddressQueryUseCase,
) : CustomerAddressesApi {

    override fun getCustomerAddressById(id: Long): ResponseEntity<CustomerAddressResponse> {
        val customerAddress = customerAddressQueryUseCase.getById(id)
        return ResponseEntity.ok(CustomerAddressQueryRestMapper.toResponse(customerAddress))
    }

    override fun listCustomerAddresses(
        page: Int,
        size: Int,
        cpfCnpj: String?,
        cnpjEmpresa: String?,
    ): ResponseEntity<CustomerAddressPage> {
        val query = ListCustomerAddressesQuery(page = page, size = size, cpfCnpjContains = cpfCnpj, cnpjEmpresa = cnpjEmpresa)
        val result = customerAddressQueryUseCase.list(query)
        return ResponseEntity.ok(CustomerAddressQueryRestMapper.toPageResponse(result))
    }
}
