package br.com.vertice.emerion_dashboard.infrastructure.rest.liberacao.mapper

import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoCommand
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoDetalheCommand
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoDetalheIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoIngestionItem
import java.time.LocalTime

object LiberacaoIngestionRestMapper {
    fun toCommand(dto: LiberacaoIngestionItem) = IngestLiberacaoCommand(
        cnpjEmpresa = dto.cnpjEmpresa,
        codigoEmpresa = dto.codigoEmpresa,
        dataPedido = dto.dataPedido,
        numeroPedido = dto.numeroPedido,
        numeroLiberacao = dto.numeroLiberacao,
        dataLiberacao = dto.dataLiberacao,
        horaLiberacao = LocalTime.parse(dto.horaLiberacao),
        codigoCliente = dto.codigoCliente,
        quantidadeSeparada = dto.quantidadeSeparada,
        totalLiberadoSemImpostos = dto.totalLiberadoSemImpostos,
        totalLiberadoComImpostos = dto.totalLiberadoComImpostos,
        situacaoLiberacao = dto.situacaoLiberacao,
        codigoVendedor = dto.codigoVendedor,
        comissaoLiberacao = dto.comissaoLiberacao,
        totalCusto = dto.totalCusto,
        detalhes = dto.detalhes.map(::toDetalheCommand),
    )

    private fun toDetalheCommand(dto: LiberacaoDetalheIngestionItem) = IngestLiberacaoDetalheCommand(
        numeroSequenciaLiberacao = dto.numeroSequenciaLiberacao,
        classificacaoItem = dto.classificacaoItem,
        codigoGrupo = dto.codigoGrupo,
        codigoSubGrupo = dto.codigoSubGrupo,
        codigoProduto = dto.codigoProduto,
        descricaoItemLiberacao = dto.descricaoItemLiberacao,
        quantidadeNoPedido = dto.quantidadeNoPedido,
        totalSeparado = dto.totalSeparado,
        quantidadeRestante = dto.quantidadeRestante,
        totalValorLiquido = dto.totalValorLiquido,
        totalValorBruto = dto.totalValorBruto,
        percentualDesconto = dto.percentualDesconto,
        totalCusto = dto.totalCusto,
        percentualDeAcrescimo = dto.percentualDeAcrescimo,
        precoVendaItem = dto.precoVendaItem,
        precoPraticado = dto.precoPraticado,
        custoPraticado = dto.custoPraticado,
    )

    fun toResponse(result: IngestLiberacaoItemResult) = IngestionItemResult(
        externalId = result.externalId,
        outcome = IngestionItemResult.Outcome.valueOf(result.outcome.name),
        errorMessage = result.errorMessage,
    )
}
