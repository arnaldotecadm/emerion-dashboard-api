package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.VendedorIngestionRestMapper.toCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.VendedorIngestionRestMapper.toItemCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.VendedorIngestionRestMapper.toItemResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.VendedorIngestionRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.IngestVendedoresService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.VendedorIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorIngestionItem
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class VendedorIngestionController(
    private val ingestVendedoresService: IngestVendedoresService,
) : VendedorIngestionApi {

    override fun ingestVendedores(vendedorIngestionBatch: VendedorIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(ingestVendedoresService.ingest(vendedorIngestionBatch.toCommand()).toResponse())

    override fun ingestSingleVendedor(vendedorIngestionItem: VendedorIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(ingestVendedoresService.ingestSingle(vendedorIngestionItem.toItemCommand()).toItemResponse())
}
