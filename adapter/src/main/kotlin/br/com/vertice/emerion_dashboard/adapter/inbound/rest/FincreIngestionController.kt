package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.FincreIngestionRestMapper.toCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.FincreIngestionRestMapper.toItemCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.FincreIngestionRestMapper.toItemResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.FincreIngestionRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.IngestFincreService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.FincreIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FincreIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FincreIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class FincreIngestionController(
    private val ingestFincreService: IngestFincreService,
) : FincreIngestionApi {
    override fun ingestFincre(fincreIngestionBatch: FincreIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(ingestFincreService.ingest(fincreIngestionBatch.toCommand()).toResponse())

    override fun ingestSingleFincre(fincreIngestionItem: FincreIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(ingestFincreService.ingestSingle(fincreIngestionItem.toItemCommand()).toItemResponse())
}
