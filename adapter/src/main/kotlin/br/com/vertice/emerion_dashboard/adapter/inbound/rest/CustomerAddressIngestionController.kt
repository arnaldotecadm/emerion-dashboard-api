package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerAddressIngestionRestMapper.toCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerAddressIngestionRestMapper.toItemCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerAddressIngestionRestMapper.toItemResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerAddressIngestionRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.IngestCustomerAddressesService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerAddressIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerAddressIngestionController(
    private val ingestCustomerAddressesService: IngestCustomerAddressesService,
) : CustomerAddressIngestionApi {

    override fun ingestCustomerAddresses(customerAddressIngestionBatch: CustomerAddressIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(ingestCustomerAddressesService.ingest(customerAddressIngestionBatch.toCommand()).toResponse())

    override fun ingestSingleCustomerAddress(customerAddressIngestionItem: CustomerAddressIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(
            ingestCustomerAddressesService.ingestSingle(customerAddressIngestionItem.toItemCommand()).toItemResponse(),
        )
}
