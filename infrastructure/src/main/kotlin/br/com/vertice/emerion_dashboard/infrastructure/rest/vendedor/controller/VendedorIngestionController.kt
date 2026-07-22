package br.com.vertice.emerion_dashboard.infrastructure.rest.vendedor.controller

import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.IngestVendedoresUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.VendedorIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.vendedor.mapper.VendedorIngestionRestMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter: implements the generated `VendedorIngestionApi`
 * contract and translates HTTP <-> application layer. Contains no business
 * logic — that lives in `IngestVendedoresUseCase`.
 */
@RestController
class VendedorIngestionController(
    private val ingestVendedoresUseCase: IngestVendedoresUseCase,
) : VendedorIngestionApi {

    override fun ingestVendedores(vendedorIngestionBatch: VendedorIngestionBatch): ResponseEntity<IngestionResult> {
        val command = VendedorIngestionRestMapper.toCommand(vendedorIngestionBatch)
        val result = ingestVendedoresUseCase.ingest(command)
        return ResponseEntity.ok(VendedorIngestionRestMapper.toResponse(result))
    }

    override fun ingestSingleVendedor(vendedorIngestionItem: VendedorIngestionItem): ResponseEntity<IngestionItemResult> {
        val command = VendedorIngestionRestMapper.toItemCommand(vendedorIngestionItem)
        val result = ingestVendedoresUseCase.ingestSingle(command)
        return ResponseEntity.ok(VendedorIngestionRestMapper.toItemResponse(result))
    }
}
