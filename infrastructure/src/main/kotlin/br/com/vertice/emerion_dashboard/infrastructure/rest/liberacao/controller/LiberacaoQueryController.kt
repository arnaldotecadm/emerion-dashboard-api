package br.com.vertice.emerion_dashboard.infrastructure.rest.liberacao.controller

import br.com.vertice.emerion_dashboard.application.liberacao.query.LiberacaoQueryUseCase
import br.com.vertice.emerion_dashboard.application.liberacao.query.model.ListLiberacoesQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.LiberacoesApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.liberacao.mapper.LiberacaoQueryRestMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class LiberacaoQueryController(
    private val liberacaoQueryUseCase: LiberacaoQueryUseCase,
) : LiberacoesApi {

    override fun getLiberacaoById(id: Long): ResponseEntity<LiberacaoResponse> =
        ResponseEntity.ok(LiberacaoQueryRestMapper.toResponse(liberacaoQueryUseCase.getById(id)))

    override fun listLiberacoes(page: Int, size: Int): ResponseEntity<LiberacaoPage> =
        ResponseEntity.ok(
            LiberacaoQueryRestMapper.toPageResponse(
                liberacaoQueryUseCase.list(ListLiberacoesQuery(page = page, size = size)),
            ),
        )

    override fun listLiberacoesByNumeroPedido(
        numeroPedido: String,
        page: Int,
        size: Int,
    ): ResponseEntity<LiberacaoPage> =
        ResponseEntity.ok(
            LiberacaoQueryRestMapper.toPageResponse(
                liberacaoQueryUseCase.list(
                    ListLiberacoesQuery(page = page, size = size, numeroPedido = numeroPedido),
                ),
            ),
        )
}
