package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerOrderIngestionRestMapper.toCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerOrderIngestionRestMapper.toItemCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerOrderIngestionRestMapper.toItemResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerOrderIngestionRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.IngestCustomerOrdersService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerOrderIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerOrderIngestionController(
    private val ingestCustomerOrdersService: IngestCustomerOrdersService,
) : CustomerOrderIngestionApi {

    override fun ingestCustomerOrders(customerOrderIngestionBatch: CustomerOrderIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(ingestCustomerOrdersService.ingest(customerOrderIngestionBatch.toCommand()).toResponse())

    override fun ingestSingleCustomerOrder(customerOrderIngestionItem: CustomerOrderIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(
            ingestCustomerOrdersService.ingestSingle(customerOrderIngestionItem.toItemCommand()).toItemResponse(),
        )
}
