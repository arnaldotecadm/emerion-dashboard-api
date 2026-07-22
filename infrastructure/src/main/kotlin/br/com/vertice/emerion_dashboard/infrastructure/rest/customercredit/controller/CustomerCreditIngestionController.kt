package br.com.vertice.emerion_dashboard.infrastructure.rest.customercredit.controller

import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.IngestCustomerCreditsUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.customercredit.mapper.CustomerCreditIngestionRestMapper
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerCreditIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerCreditIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter: implements the generated
 * `CustomerCreditIngestionApi` contract and translates HTTP <-> application
 * layer. Contains no business logic — that lives in
 * `IngestCustomerCreditsUseCase`.
 */
@RestController
class CustomerCreditIngestionController(
    private val ingestCustomerCreditsUseCase: IngestCustomerCreditsUseCase,
) : CustomerCreditIngestionApi {

    override fun ingestCustomerCreditsBatch(customerCreditIngestionItem: List<CustomerCreditIngestionItem>): ResponseEntity<IngestionResult> {
        val command = CustomerCreditIngestionRestMapper.toCommand(customerCreditIngestionItem)
        val result = ingestCustomerCreditsUseCase.ingest(command)
        return ResponseEntity.ok(CustomerCreditIngestionRestMapper.toResponse(result))
    }

    override fun ingestSingleCustomerCredit(customerCreditIngestionItem: CustomerCreditIngestionItem): ResponseEntity<IngestionItemResult> {
        val command = CustomerCreditIngestionRestMapper.toItemCommand(customerCreditIngestionItem)
        val result = ingestCustomerCreditsUseCase.ingestSingle(command)
        return ResponseEntity.ok(CustomerCreditIngestionRestMapper.toItemResponse(result))
    }
}
