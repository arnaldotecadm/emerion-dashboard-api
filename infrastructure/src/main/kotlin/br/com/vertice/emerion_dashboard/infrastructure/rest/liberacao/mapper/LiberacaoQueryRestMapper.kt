package br.com.vertice.emerion_dashboard.infrastructure.rest.liberacao.mapper

import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.model.LiberacaoDetalhe
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoDetalheResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import java.time.ZoneOffset

object LiberacaoQueryRestMapper {
    fun toResponse(liberacao: Liberacao): LiberacaoResponse =
        LiberacaoResponse(
            id = requireNotNull(liberacao.id),
            cnpjEmpresa = liberacao.cnpjEmpresa,
            codigoEmpresa = liberacao.codigoEmpresa,
            dataPedido = liberacao.dataPedido,
            numeroPedido = liberacao.numeroPedido,
            numeroLiberacao = liberacao.numeroLiberacao,
            dataLiberacao = liberacao.dataLiberacao,
            horaLiberacao = liberacao.horaLiberacao.toString(),
            codigoCliente = liberacao.codigoCliente,
            quantidadeSeparada = liberacao.quantidadeSeparada,
            totalLiberadoSemImpostos = liberacao.totalLiberadoSemImpostos,
            totalLiberadoComImpostos = liberacao.totalLiberadoComImpostos,
            situacaoLiberacao = liberacao.situacaoLiberacao,
            codigoVendedor = liberacao.codigoVendedor,
            comissaoLiberacao = liberacao.comissaoLiberacao,
            totalCusto = liberacao.totalCusto,
            detalhes = liberacao.detalhes.map(::toDetalheResponse),
            createdAt = liberacao.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = liberacao.updatedAt.atOffset(ZoneOffset.UTC),
        )

    fun toPageResponse(page: DomainPage<Liberacao>): LiberacaoPage =
        LiberacaoPage(
            data = page.content.map(::toResponse),
            pagination = PaginationInfo(
                total = page.totalElements,
                page = page.page,
                propertySize = page.size,
                totalPages = page.totalPages,
            ),
        )

    private fun toDetalheResponse(detalhe: LiberacaoDetalhe): LiberacaoDetalheResponse =
        LiberacaoDetalheResponse(
            numeroSequenciaLiberacao = detalhe.numeroSequenciaLiberacao,
            classificacaoItem = detalhe.classificacaoItem,
            codigoGrupo = detalhe.codigoGrupo,
            codigoSubGrupo = detalhe.codigoSubGrupo,
            codigoProduto = detalhe.codigoProduto,
            descricaoItemLiberacao = detalhe.descricaoItemLiberacao,
            quantidadeNoPedido = detalhe.quantidadeNoPedido,
            totalSeparado = detalhe.totalSeparado,
            quantidadeRestante = detalhe.quantidadeRestante,
            totalValorLiquido = detalhe.totalValorLiquido,
            totalValorBruto = detalhe.totalValorBruto,
            percentualDesconto = detalhe.percentualDesconto,
            totalCusto = detalhe.totalCusto,
            percentualDeAcrescimo = detalhe.percentualDeAcrescimo,
            precoVendaItem = detalhe.precoVendaItem,
            precoPraticado = detalhe.precoPraticado,
            custoPraticado = detalhe.custoPraticado,
        )
}
