package br.com.vertice.emerion_dashboard.infrastructure.rest.customercredit.controller

import br.com.vertice.emerion_dashboard.application.customercredit.query.CustomerCreditQueryUseCase
import br.com.vertice.emerion_dashboard.application.customercredit.query.model.ListCustomerCreditsQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.customercredit.mapper.CustomerCreditQueryRestMapper
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerCreditsApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerCreditPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerCreditResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter for the customer credit read endpoints consumed
 * by the React frontend. Implements the generated `CustomerCreditsApi`
 * contract, contains no business logic.
 */
@RestController
class CustomerCreditQueryController(
    private val customerCreditQueryUseCase: CustomerCreditQueryUseCase,
) : CustomerCreditsApi {

    override fun getCustomerCreditById(id: Long): ResponseEntity<CustomerCreditResponse> {
        val customerCredit = customerCreditQueryUseCase.getById(id)
        return ResponseEntity.ok(CustomerCreditQueryRestMapper.toResponse(customerCredit))
    }

    override fun listCustomerCredits(
        page: Int,
        size: Int,
        customerExternalId: String?,
        tipo: String?,
        cnpjEmpresa: String?,
    ): ResponseEntity<CustomerCreditPage> {
        val query = ListCustomerCreditsQuery(
            page = page,
            size = size,
            customerExternalId = customerExternalId,
            tipo = tipo,
            cnpjEmpresa = cnpjEmpresa,
        )
        val result = customerCreditQueryUseCase.list(query)
        return ResponseEntity.ok(CustomerCreditQueryRestMapper.toPageResponse(result))
    }
}
