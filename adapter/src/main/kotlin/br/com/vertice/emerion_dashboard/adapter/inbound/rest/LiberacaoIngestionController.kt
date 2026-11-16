package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.LiberacaoIngestionRestMapper.toCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.LiberacaoIngestionRestMapper.toCommands
import br.com.vertice.emerion_dashboard.adapter.mapper.LiberacaoIngestionRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.IngestLiberacaoService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.LiberacaoIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoIngestionItem
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class LiberacaoIngestionController(
    private val ingestLiberacaoService: IngestLiberacaoService,
) : LiberacaoIngestionApi {
    override fun ingestLiberacoes(liberacaoIngestionBatch: LiberacaoIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(
            ingestLiberacaoService.ingest(liberacaoIngestionBatch.batchId, liberacaoIngestionBatch.toCommands()).toResponse(),
        )

    override fun ingestSingleLiberacao(liberacaoIngestionItem: LiberacaoIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(ingestLiberacaoService.ingestSingle(liberacaoIngestionItem.toCommand()).toResponse())
}
