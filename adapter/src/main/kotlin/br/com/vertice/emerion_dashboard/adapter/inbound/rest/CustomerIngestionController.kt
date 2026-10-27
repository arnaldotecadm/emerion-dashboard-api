package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerIngestionRestMapper.toCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerIngestionRestMapper.toItemCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerIngestionRestMapper.toItemResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerIngestionRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.customer.ingestion.IngestCustomersService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerIngestionController(
    private val ingestCustomersService: IngestCustomersService,
) : CustomerIngestionApi {

    override fun ingestCustomers(customerIngestionBatch: CustomerIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(ingestCustomersService.ingest(customerIngestionBatch.toCommand()).toResponse())

    override fun ingestSingleCustomer(customerIngestionItem: CustomerIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(ingestCustomersService.ingestSingle(customerIngestionItem.toItemCommand()).toItemResponse())
}
