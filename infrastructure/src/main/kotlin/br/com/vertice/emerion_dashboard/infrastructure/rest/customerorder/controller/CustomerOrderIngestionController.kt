package br.com.vertice.emerion_dashboard.infrastructure.rest.customerorder.controller

import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.IngestCustomerOrdersUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.customerorder.mapper.CustomerOrderIngestionRestMapper
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerOrderIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter: implements the generated
 * `CustomerOrderIngestionApi` contract and translates HTTP <-> application
 * layer. Contains no business logic — that lives in
 * `IngestCustomerOrdersUseCase`.
 */
@RestController
class CustomerOrderIngestionController(
    private val ingestCustomerOrdersUseCase: IngestCustomerOrdersUseCase,
) : CustomerOrderIngestionApi {

    override fun ingestCustomerOrders(customerOrderIngestionBatch: CustomerOrderIngestionBatch): ResponseEntity<IngestionResult> {
        val command = CustomerOrderIngestionRestMapper.toCommand(customerOrderIngestionBatch)
        val result = ingestCustomerOrdersUseCase.ingest(command)
        return ResponseEntity.ok(CustomerOrderIngestionRestMapper.toResponse(result))
    }

    override fun ingestSingleCustomerOrder(customerOrderIngestionItem: CustomerOrderIngestionItem): ResponseEntity<IngestionItemResult> {
        val command = CustomerOrderIngestionRestMapper.toItemCommand(customerOrderIngestionItem)
        val result = ingestCustomerOrdersUseCase.ingestSingle(command)
        return ResponseEntity.ok(CustomerOrderIngestionRestMapper.toItemResponse(result))
    }
}
