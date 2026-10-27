package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.LiberacaoQueryRestMapper.toPageResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.LiberacaoQueryRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.liberacao.query.LiberacaoQueryService
import br.com.vertice.emerion_dashboard.application.liberacao.query.model.ListLiberacoesQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.LiberacoesApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class LiberacaoQueryController(
    private val liberacaoQueryService: LiberacaoQueryService,
) : LiberacoesApi {

    override fun getLiberacaoById(id: Long): ResponseEntity<LiberacaoResponse> =
        ResponseEntity.ok(liberacaoQueryService.getById(id).toResponse())

    override fun listLiberacoes(page: Int, size: Int): ResponseEntity<LiberacaoPage> =
        ResponseEntity.ok(liberacaoQueryService.list(ListLiberacoesQuery(page = page, size = size)).toPageResponse())

    override fun listLiberacoesByNumeroPedido(
        numeroPedido: String,
        page: Int,
        size: Int,
    ): ResponseEntity<LiberacaoPage> =
        ResponseEntity.ok(
            liberacaoQueryService.list(
                ListLiberacoesQuery(page = page, size = size, numeroPedido = numeroPedido),
            ).toPageResponse(),
        )
}
