package br.com.vertice.emerion_dashboard.infrastructure.rest.ipi.controller

import br.com.vertice.emerion_dashboard.application.ipi.ingestion.IngestIpiUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.IpiIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IpiIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IpiIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.ipi.mapper.IpiIngestionRestMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class IpiIngestionController(
    private val ingestIpiUseCase: IngestIpiUseCase,
) : IpiIngestionApi {
    override fun ingestIpi(ipiIngestionBatch: IpiIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(IpiIngestionRestMapper.toResponse(ingestIpiUseCase.ingest(IpiIngestionRestMapper.toCommand(ipiIngestionBatch))))

    override fun ingestSingleIpi(ipiIngestionItem: IpiIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(IpiIngestionRestMapper.toItemResponse(ingestIpiUseCase.ingestSingle(IpiIngestionRestMapper.toItemCommand(ipiIngestionItem))))
}
