package br.com.vertice.emerion_dashboard.infrastructure.rest.customeraddress.controller

import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.IngestCustomerAddressesUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.customeraddress.mapper.CustomerAddressIngestionRestMapper
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerAddressIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter: implements the generated
 * `CustomerAddressIngestionApi` contract and translates HTTP <-> application
 * layer. Contains no business logic — that lives in
 * `IngestCustomerAddressesUseCase`.
 */
@RestController
class CustomerAddressIngestionController(
    private val ingestCustomerAddressesUseCase: IngestCustomerAddressesUseCase,
) : CustomerAddressIngestionApi {

    override fun ingestCustomerAddresses(customerAddressIngestionBatch: CustomerAddressIngestionBatch): ResponseEntity<IngestionResult> {
        val command = CustomerAddressIngestionRestMapper.toCommand(customerAddressIngestionBatch)
        val result = ingestCustomerAddressesUseCase.ingest(command)
        return ResponseEntity.ok(CustomerAddressIngestionRestMapper.toResponse(result))
    }

    override fun ingestSingleCustomerAddress(customerAddressIngestionItem: CustomerAddressIngestionItem): ResponseEntity<IngestionItemResult> {
        val command = CustomerAddressIngestionRestMapper.toItemCommand(customerAddressIngestionItem)
        val result = ingestCustomerAddressesUseCase.ingestSingle(command)
        return ResponseEntity.ok(CustomerAddressIngestionRestMapper.toItemResponse(result))
    }
}
