package br.com.vertice.emerion_dashboard.infrastructure.rest.icms.controller

import br.com.vertice.emerion_dashboard.application.icms.ingestion.IngestIcmsUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.IcmsIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IcmsIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IcmsIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.icms.mapper.IcmsIngestionRestMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class IcmsIngestionController(
    private val ingestIcmsUseCase: IngestIcmsUseCase,
) : IcmsIngestionApi {
    override fun ingestIcms(icmsIngestionBatch: IcmsIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(IcmsIngestionRestMapper.toResponse(ingestIcmsUseCase.ingest(IcmsIngestionRestMapper.toCommand(icmsIngestionBatch))))

    override fun ingestSingleIcms(icmsIngestionItem: IcmsIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(IcmsIngestionRestMapper.toItemResponse(ingestIcmsUseCase.ingestSingle(IcmsIngestionRestMapper.toItemCommand(icmsIngestionItem))))
}
