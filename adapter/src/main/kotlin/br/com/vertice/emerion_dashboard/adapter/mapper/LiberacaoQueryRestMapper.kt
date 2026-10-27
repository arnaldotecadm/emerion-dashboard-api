package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.model.LiberacaoDetalhe
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoDetalheResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import java.time.ZoneOffset

object LiberacaoQueryRestMapper {
    fun Liberacao.toResponse(): LiberacaoResponse =
        LiberacaoResponse(
            id = requireNotNull(id),
            cnpjEmpresa = cnpjEmpresa,
            codigoEmpresa = codigoEmpresa,
            dataPedido = dataPedido,
            numeroPedido = numeroPedido,
            numeroLiberacao = numeroLiberacao,
            dataLiberacao = dataLiberacao,
            horaLiberacao = horaLiberacao.toString(),
            codigoCliente = codigoCliente,
            quantidadeSeparada = quantidadeSeparada,
            totalLiberadoSemImpostos = totalLiberadoSemImpostos,
            totalLiberadoComImpostos = totalLiberadoComImpostos,
            situacaoLiberacao = situacaoLiberacao,
            codigoVendedor = codigoVendedor,
            comissaoLiberacao = comissaoLiberacao,
            totalCusto = totalCusto,
            detalhes = detalhes.map { it.toDetalheResponse() },
            createdAt = createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = updatedAt.atOffset(ZoneOffset.UTC),
        )

    fun DomainPage<Liberacao>.toPageResponse(): LiberacaoPage =
        LiberacaoPage(
            data = content.map { it.toResponse() },
            pagination = PaginationInfo(
                total = totalElements,
                page = page,
                propertySize = size,
                totalPages = totalPages,
            ),
        )

    private fun LiberacaoDetalhe.toDetalheResponse(): LiberacaoDetalheResponse =
        LiberacaoDetalheResponse(
            numeroSequenciaLiberacao = numeroSequenciaLiberacao,
            classificacaoItem = classificacaoItem,
            codigoGrupo = codigoGrupo,
            codigoSubGrupo = codigoSubGrupo,
            codigoProduto = codigoProduto,
            descricaoItemLiberacao = descricaoItemLiberacao,
            quantidadeNoPedido = quantidadeNoPedido,
            totalSeparado = totalSeparado,
            quantidadeRestante = quantidadeRestante,
            totalValorLiquido = totalValorLiquido,
            totalValorBruto = totalValorBruto,
            percentualDesconto = percentualDesconto,
            totalCusto = totalCusto,
            percentualDeAcrescimo = percentualDeAcrescimo,
            precoVendaItem = precoVendaItem,
            precoPraticado = precoPraticado,
            custoPraticado = custoPraticado,
        )
}
