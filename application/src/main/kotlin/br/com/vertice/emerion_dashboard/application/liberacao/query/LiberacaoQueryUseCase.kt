package br.com.vertice.emerion_dashboard.application.liberacao.query

import br.com.vertice.emerion_dashboard.application.liberacao.query.model.ListLiberacoesQuery
import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.shared.Page

interface LiberacaoQueryUseCase {
    fun getById(id: Long): Liberacao
    fun list(query: ListLiberacoesQuery): Page<Liberacao>
}
