package br.com.vertice.emerion_dashboard.infrastructure.rest.liberacao.controller

import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.IngestLiberacaoUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.LiberacaoIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.liberacao.mapper.LiberacaoIngestionRestMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class LiberacaoIngestionController(
    private val ingestLiberacaoUseCase: IngestLiberacaoUseCase,
) : LiberacaoIngestionApi {
    override fun ingestSingleLiberacao(liberacaoIngestionItem: LiberacaoIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(
            LiberacaoIngestionRestMapper.toResponse(
                ingestLiberacaoUseCase.ingestSingle(LiberacaoIngestionRestMapper.toCommand(liberacaoIngestionItem)),
            ),
        )
}
