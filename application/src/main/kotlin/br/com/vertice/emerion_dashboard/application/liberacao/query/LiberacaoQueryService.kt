package br.com.vertice.emerion_dashboard.application.liberacao.query

import br.com.vertice.emerion_dashboard.application.liberacao.query.model.ListLiberacoesQuery
import br.com.vertice.emerion_dashboard.domain.liberacao.exception.LiberacaoNotFoundException
import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.repository.LiberacaoRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LiberacaoQueryService(
    private val liberacaoRepository: LiberacaoRepository,
) : LiberacaoQueryUseCase {

    @Transactional(readOnly = true)
    override fun getById(id: Long): Liberacao =
        liberacaoRepository.findById(id) ?: throw LiberacaoNotFoundException(id)

    @Transactional(readOnly = true)
    override fun list(query: ListLiberacoesQuery): Page<Liberacao> =
        liberacaoRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            numeroPedido = query.numeroPedido,
        )
}
