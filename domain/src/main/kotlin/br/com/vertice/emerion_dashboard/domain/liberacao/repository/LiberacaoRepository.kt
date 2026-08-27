package br.com.vertice.emerion_dashboard.domain.liberacao.repository

import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest

interface LiberacaoRepository {
    fun findById(id: Long): Liberacao?
    fun findByNumeroPedidoAndNumeroLiberacao(numeroPedido: String, numeroLiberacao: Int): Liberacao?
    fun findAll(pageRequest: PageRequest, numeroPedido: String? = null): Page<Liberacao>
    fun save(liberacao: Liberacao): Liberacao
}
