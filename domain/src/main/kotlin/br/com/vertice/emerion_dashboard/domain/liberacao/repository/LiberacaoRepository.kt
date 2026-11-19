package br.com.vertice.emerion_dashboard.domain.liberacao.repository

import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.model.TopSoldItem
import br.com.vertice.emerion_dashboard.domain.liberacao.model.TopLiberacaoCustomer
import br.com.vertice.emerion_dashboard.domain.liberacao.model.LiberacaoSalesByUf
import br.com.vertice.emerion_dashboard.domain.liberacao.model.LiberacaoSalespersonSummary
import br.com.vertice.emerion_dashboard.domain.liberacao.model.LiberacaoSalespersonTotals
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import java.time.LocalDate

interface LiberacaoRepository {
    fun findById(id: Long): Liberacao?
    fun findByNumeroPedidoAndNumeroLiberacao(numeroPedido: Long, numeroLiberacao: Int): Liberacao?
    fun findAll(pageRequest: PageRequest, numeroPedido: Long? = null): Page<Liberacao>
    fun findTopSoldItems(startDate: LocalDate, endDate: LocalDate): List<TopSoldItem>
    fun findTopCustomers(startDate: LocalDate, endDate: LocalDate): List<TopLiberacaoCustomer>
    fun findSalesByUf(startDate: LocalDate, endDate: LocalDate): List<LiberacaoSalesByUf>
    fun findSalespeople(startDate: LocalDate, endDate: LocalDate): List<LiberacaoSalespersonSummary>
    fun findSalespersonTotals(startDate: LocalDate, endDate: LocalDate): List<LiberacaoSalespersonTotals>
    fun save(liberacao: Liberacao): Liberacao
}
