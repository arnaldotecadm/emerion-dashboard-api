package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerQuotationIngestionRestMapper.toCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerQuotationIngestionRestMapper.toItemCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerQuotationIngestionRestMapper.toItemResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerQuotationIngestionRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.customerquotation.ingestion.IngestCustomerQuotationService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerQuotationIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerQuotationIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerQuotationIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerQuotationIngestionController(
    private val ingestCustomerQuotationService: IngestCustomerQuotationService,
) : CustomerQuotationIngestionApi {
    override fun ingestCustomerQuotations(
        customerQuotationIngestionBatch: CustomerQuotationIngestionBatch,
    ): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(ingestCustomerQuotationService.ingest(customerQuotationIngestionBatch.toCommand()).toResponse())

    override fun ingestSingleCustomerQuotation(
        customerQuotationIngestionItem: CustomerQuotationIngestionItem,
    ): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(
            ingestCustomerQuotationService.ingestSingle(customerQuotationIngestionItem.toItemCommand()).toItemResponse(),
        )
}
