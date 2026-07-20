package br.com.vertice.emerion_dashboard.infrastructure.rest.customer

import br.com.vertice.emerion_dashboard.application.customer.IngestCustomersUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomerIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter: implements the generated `CustomerIngestionApi`
 * contract and translates HTTP <-> application layer. Contains no business
 * logic — that lives in `IngestCustomersUseCase`.
 */
@RestController
class CustomerIngestionController(
    private val ingestCustomersUseCase: IngestCustomersUseCase,
) : CustomerIngestionApi {

    override fun ingestCustomers(customerIngestionBatch: CustomerIngestionBatch): ResponseEntity<IngestionResult> {
        val command = CustomerIngestionRestMapper.toCommand(customerIngestionBatch)
        val result = ingestCustomersUseCase.ingest(command)
        return ResponseEntity.ok(CustomerIngestionRestMapper.toResponse(result))
    }
}
