package br.com.vertice.emerion_dashboard.infrastructure.rest.fincre.controller

import br.com.vertice.emerion_dashboard.application.fincre.ingestion.IngestFincreUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.fincre.mapper.FincreIngestionRestMapper
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.FincreIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FincreIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FincreIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class FincreIngestionController(
    private val ingestFincreUseCase: IngestFincreUseCase,
) : FincreIngestionApi {
    override fun ingestFincre(fincreIngestionBatch: FincreIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(
            FincreIngestionRestMapper.toResponse(
                ingestFincreUseCase.ingest(FincreIngestionRestMapper.toCommand(fincreIngestionBatch)),
            ),
        )

    override fun ingestSingleFincre(fincreIngestionItem: FincreIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(
            FincreIngestionRestMapper.toItemResponse(
                ingestFincreUseCase.ingestSingle(FincreIngestionRestMapper.toItemCommand(fincreIngestionItem)),
            ),
        )
}
