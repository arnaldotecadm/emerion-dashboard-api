package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.IcmsIngestionRestMapper.toCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.IcmsIngestionRestMapper.toItemCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.IcmsIngestionRestMapper.toItemResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.IcmsIngestionRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.icms.ingestion.IngestIcmsService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.IcmsIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IcmsIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IcmsIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class IcmsIngestionController(
    private val ingestIcmsService: IngestIcmsService,
) : IcmsIngestionApi {
    override fun ingestIcms(icmsIngestionBatch: IcmsIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(ingestIcmsService.ingest(icmsIngestionBatch.toCommand()).toResponse())

    override fun ingestSingleIcms(icmsIngestionItem: IcmsIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(ingestIcmsService.ingestSingle(icmsIngestionItem.toItemCommand()).toItemResponse())
}
