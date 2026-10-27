package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerCreditIngestionRestMapper.toCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerCreditIngestionRestMapper.toItemCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerCreditIngestionRestMapper.toItemResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerCreditIngestionRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.IngestCustomerCreditsService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerCreditIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerCreditIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerCreditIngestionController(
    private val ingestCustomerCreditsService: IngestCustomerCreditsService,
) : CustomerCreditIngestionApi {

    override fun ingestCustomerCreditsBatch(customerCreditIngestionItem: List<CustomerCreditIngestionItem>): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(ingestCustomerCreditsService.ingest(customerCreditIngestionItem.toCommand()).toResponse())

    override fun ingestSingleCustomerCredit(customerCreditIngestionItem: CustomerCreditIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(
            ingestCustomerCreditsService.ingestSingle(customerCreditIngestionItem.toItemCommand()).toItemResponse(),
        )
}
