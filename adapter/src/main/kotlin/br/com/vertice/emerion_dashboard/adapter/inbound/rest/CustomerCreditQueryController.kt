package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerCreditQueryRestMapper.toPageResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerCreditQueryRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.customercredit.query.CustomerCreditQueryService
import br.com.vertice.emerion_dashboard.application.customercredit.query.model.ListCustomerCreditsQuery
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
    private val customerCreditQueryService: CustomerCreditQueryService,
) : CustomerCreditsApi {

    override fun getCustomerCreditById(id: Long): ResponseEntity<CustomerCreditResponse> =
        ResponseEntity.ok(customerCreditQueryService.getById(id).toResponse())

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
        return ResponseEntity.ok(customerCreditQueryService.list(query).toPageResponse())
    }
}
