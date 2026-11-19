package br.com.vertice.emerion_dashboard.application.liberacao.query

import br.com.vertice.emerion_dashboard.application.liberacao.query.model.ListLiberacoesQuery
import br.com.vertice.emerion_dashboard.domain.exception.LiberacaoNotFoundException
import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.repository.LiberacaoRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class LiberacaoQueryService(
    private val liberacaoRepository: LiberacaoRepository,
) {

    @Transactional(readOnly = true)
    fun getById(id: Long): Liberacao =
        liberacaoRepository.findById(id) ?: throw LiberacaoNotFoundException(id)

    @Transactional(readOnly = true)
    fun list(query: ListLiberacoesQuery): Page<Liberacao> =
        liberacaoRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            numeroPedido = query.numeroPedido,
        )

    @Transactional(readOnly = true)
    fun getTopSoldItems(startDate: LocalDate, endDate: LocalDate) =
        liberacaoRepository.findTopSoldItems(startDate, endDate)

    @Transactional(readOnly = true)
    fun getTopCustomers(startDate: LocalDate, endDate: LocalDate) =
        liberacaoRepository.findTopCustomers(startDate, endDate)

    @Transactional(readOnly = true)
    fun getSalesByUf(startDate: LocalDate, endDate: LocalDate) =
        liberacaoRepository.findSalesByUf(startDate, endDate)

    @Transactional(readOnly = true)
    fun getSalespeople(startDate: LocalDate, endDate: LocalDate) =
        liberacaoRepository.findSalespeople(startDate, endDate)

    @Transactional(readOnly = true)
    fun getSalespersonTotals(startDate: LocalDate, endDate: LocalDate) =
        liberacaoRepository.findSalespersonTotals(startDate, endDate)

    @Transactional(readOnly = true)
    fun getFaturamentoResumo(startDate: LocalDate, endDate: LocalDate) =
        br.com.vertice.emerion_dashboard.domain.liberacao.model.FaturamentoResumo(
            topSoldItems = getTopSoldItems(startDate, endDate),
            topCustomers = getTopCustomers(startDate, endDate),
            salesByUf = getSalesByUf(startDate, endDate),
            salespeople = getSalespeople(startDate, endDate),
            salespersonTotals = getSalespersonTotals(startDate, endDate),
        )
}
