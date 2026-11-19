package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoCommand
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoDetalheCommand
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoItemResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoDetalheIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoIngestionItem
import java.time.LocalTime

object LiberacaoIngestionRestMapper {
    fun LiberacaoIngestionBatch.toCommands(): List<IngestLiberacaoCommand> = items.map { it.toCommand() }

    fun LiberacaoIngestionItem.toCommand() = IngestLiberacaoCommand(
        cnpjEmpresa = cnpjEmpresa,
        codigoEmpresa = codigoEmpresa,
        dataPedido = dataPedido,
        dataFaturamento = dataFaturamento,
        numeroPedido = numeroPedido,
        numeroLiberacao = numeroLiberacao,
        dataLiberacao = dataLiberacao,
        horaLiberacao = LocalTime.parse(horaLiberacao),
        codigoCliente = codigoCliente,
        quantidadeSeparada = quantidadeSeparada,
        totalLiberadoSemImpostos = totalLiberadoSemImpostos,
        totalLiberadoComImpostos = totalLiberadoComImpostos,
        situacaoLiberacao = situacaoLiberacao,
        codigoVendedor = codigoVendedor,
        comissaoLiberacao = comissaoLiberacao,
        totalCusto = totalCusto,
        detalhes = detalhes.map { it.toDetalheCommand() },
    )

    private fun LiberacaoDetalheIngestionItem.toDetalheCommand() = IngestLiberacaoDetalheCommand(
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

    fun IngestLiberacaoItemResult.toResponse() = IngestionItemResult(
        externalId = externalId,
        outcome = IngestionItemResult.Outcome.valueOf(outcome.name),
        errorMessage = errorMessage,
    )

    fun IngestBatchResult.toResponse() = IngestionResult(
        batchId = batchId,
        totalReceived = totalReceived,
        totalSucceeded = totalSucceeded,
        totalFailed = totalFailed,
        results = results.map {
            IngestionItemResult(
                externalId = it.externalId,
                outcome = IngestionItemResult.Outcome.valueOf(it.outcome.name),
                errorMessage = it.errorMessage,
            )
        },
    )
}
