package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.FaturamentoRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.liberacao.query.LiberacaoQueryService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.FaturamentoApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FaturamentoResumoResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class FaturamentoController(
    private val liberacaoQueryService: LiberacaoQueryService,
) : FaturamentoApi {
    override fun getFaturamentoResumo(
        startDate: LocalDate,
        endDate: LocalDate,
    ): ResponseEntity<FaturamentoResumoResponse> =
        ResponseEntity.ok(liberacaoQueryService.getFaturamentoResumo(startDate, endDate).toResponse())
}
