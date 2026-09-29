package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.IpiIngestionRestMapper.toCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.IpiIngestionRestMapper.toItemCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.IpiIngestionRestMapper.toItemResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.IpiIngestionRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.IngestIpiService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.IpiIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IpiIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IpiIngestionItem
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class IpiIngestionController(
    private val ingestIpiService: IngestIpiService,
) : IpiIngestionApi {
    override fun ingestIpi(ipiIngestionBatch: IpiIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(ingestIpiService.ingest(ipiIngestionBatch.toCommand()).toResponse())

    override fun ingestSingleIpi(ipiIngestionItem: IpiIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(ingestIpiService.ingestSingle(ipiIngestionItem.toItemCommand()).toItemResponse())
}
