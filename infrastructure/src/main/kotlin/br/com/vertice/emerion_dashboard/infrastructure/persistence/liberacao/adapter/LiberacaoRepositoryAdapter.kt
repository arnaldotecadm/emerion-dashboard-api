package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.adapter

import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.repository.LiberacaoRepository
import br.com.vertice.emerion_dashboard.application.cognitouser.UserService
import br.com.vertice.emerion_dashboard.domain.liberacao.model.TopLiberacaoCustomer
import br.com.vertice.emerion_dashboard.domain.liberacao.model.TopSoldItem
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.mapper.LiberacaoPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.TopSoldItemProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.TopLiberacaoCustomerProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoSalesByUfProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoSalespersonSummaryProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoSalespersonTotalsProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.repository.LiberacaoQueryRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.repository.LiberacaoSpringDataRepository
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class LiberacaoRepositoryAdapter(
    private val springDataRepository: LiberacaoSpringDataRepository,
    private val queryRepository: LiberacaoQueryRepository,
    private val userService: UserService,
) : LiberacaoRepository {
    override fun findById(id: Long): Liberacao? =
        queryRepository.findProjectionById(id, userService.getCurrentCompanyCnpj())?.let(::toDomain)

    override fun findByNumeroPedidoAndNumeroLiberacao(numeroPedido: Long, numeroLiberacao: Int): Liberacao? =
        springDataRepository.findByNumeroPedidoAndNumeroLiberacao(numeroPedido, numeroLiberacao)
            ?.let(LiberacaoPersistenceMapper::toDomain)

    override fun findAll(pageRequest: PageRequest, numeroPedido: Long?): Page<Liberacao> {
        val result = queryRepository.search(
            numeroPedido,
            userService.getCurrentCompanyCnpj(),
            SpringPageRequest.of(pageRequest.page, pageRequest.size),
        )
        return Page(
            content = result.content.map(::toDomain),
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = result.totalElements,
        )
    }

    override fun save(liberacao: Liberacao): Liberacao {
        val existing = springDataRepository.findByNumeroPedidoAndNumeroLiberacao(
            liberacao.numeroPedido,
            liberacao.numeroLiberacao,
        )
        return springDataRepository.save(LiberacaoPersistenceMapper.toEntity(liberacao, existing))
            .let(LiberacaoPersistenceMapper::toDomain)
    }

    override fun findTopSoldItems(startDate: LocalDate, endDate: LocalDate) =
        queryRepository.findTopSoldItems(startDate, endDate, userService.getCurrentCompanyCnpj())
            .map(::toDomain)

    override fun findTopCustomers(startDate: LocalDate, endDate: LocalDate) =
        queryRepository.findTopCustomers(startDate, endDate, userService.getCurrentCompanyCnpj())
            .map(::toDomain)

    override fun findSalesByUf(startDate: LocalDate, endDate: LocalDate) =
        queryRepository.findSalesByUf(startDate, endDate, userService.getCurrentCompanyCnpj())
            .map(::toDomain)

    override fun findSalespeople(startDate: LocalDate, endDate: LocalDate) =
        queryRepository.findSalespeople(startDate, endDate, userService.getCurrentCompanyCnpj())
            .map(::toDomain)

    override fun findSalespersonTotals(startDate: LocalDate, endDate: LocalDate) =
        queryRepository.findSalespersonTotals(startDate, endDate, userService.getCurrentCompanyCnpj())
            .map(::toDomain)

    private fun toDomain(projection: LiberacaoProjection): Liberacao =
        LiberacaoPersistenceMapper.toDomain(
            projection,
            queryRepository.findDetalhes(projection.numeroPedido, projection.numeroLiberacao),
        )

    private fun toDomain(projection: TopSoldItemProjection) =
        TopSoldItem(
            codigoGrupo = projection.codigoGrupo,
            codigoSubGrupo = projection.codigoSubGrupo,
            codigoProduto = projection.codigoProduto,
            descricaoItemLiberacao = projection.descricaoItemLiberacao,
            totalSeparado = projection.totalSeparado,
        )

    private fun toDomain(projection: TopLiberacaoCustomerProjection) =
        TopLiberacaoCustomer(
            codigoCliente = projection.codigoCliente,
            razaoSocial = projection.razaoSocial,
            totalLiberadoComImpostos = projection.totalLiberadoComImpostos,
        )

    private fun toDomain(projection: LiberacaoSalesByUfProjection) =
        br.com.vertice.emerion_dashboard.domain.liberacao.model.LiberacaoSalesByUf(
            uf = projection.uf,
            amount = projection.amount,
            representationPercentage = projection.representationPercentage,
        )

    private fun toDomain(projection: LiberacaoSalespersonSummaryProjection) =
        br.com.vertice.emerion_dashboard.domain.liberacao.model.LiberacaoSalespersonSummary(
            codigoVendedor = projection.codigoVendedor,
            nomeVendedor = projection.nomeVendedor,
            clientesResponsaveis = projection.clientesResponsaveis,
            clientesComCompra = projection.clientesComCompra,
            representationPercentage = projection.representationPercentage,
        )

    private fun toDomain(projection: LiberacaoSalespersonTotalsProjection) =
        br.com.vertice.emerion_dashboard.domain.liberacao.model.LiberacaoSalespersonTotals(
            codigoVendedor = projection.codigoVendedor,
            nomeVendedor = projection.nomeVendedor,
            totalFaturado = projection.totalFaturado,
            totalComissao = projection.totalComissao,
            totalDespesas = projection.totalDespesas,
            custosTotais = projection.custosTotais,
            totalIcms = projection.totalIcms,
            totalSemNota = projection.totalSemNota,
        )
}
