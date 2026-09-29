package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreBatchCommand
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreCommand
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreParcelaCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FincreIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FincreIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FincreParcelaIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

/** Maps between the generated ingestion DTOs and the application command/result models. */
object FincreIngestionRestMapper {

    fun FincreIngestionBatch.toCommand(): IngestFincreBatchCommand =
        IngestFincreBatchCommand(batchId, items.map { it.toItemCommand() })

    fun FincreIngestionItem.toItemCommand(): IngestFincreCommand = IngestFincreCommand(
        cnpjEmpresa = cnpjEmpresa,
        codigoEmpresa = codigoEmpresa,
        dataEmissao = dataEmissao,
        documento = documento,
        codigoCondicaoRecebimento = codigoCondicaoRecebimento,
        nomeCondicaoRecebimento = nomeCondicaoRecebimento,
        nomeEmpresa = nomeEmpresa,
        codigoComissao = codigoComissao,
        percentualComissao = percentualComissao,
        codigoCliente = codigoCliente,
        nomeCliente = nomeCliente,
        codigoVendedor = codigoVendedor,
        nomeVendedor = nomeVendedor,
        codigoTipoDocumento = codigoTipoDocumento,
        nomeTipoDocumento = nomeTipoDocumento,
        parcelas = parcelas.map { it.toCommand() },
    )

    private fun FincreParcelaIngestionItem.toCommand() = IngestFincreParcelaCommand(
        numeroParcela = numeroParcela,
        flagIncobravel = flagIncobravel,
        dataIncobravel = dataIncobravel,
        dataVencimento = dataVencimento,
        prazoEmDias = prazoEmDias,
        valorParcela = valorParcela,
        numeroBancario = numeroBancario,
        codigoBanco = codigoBanco,
        nomeBanco = nomeBanco,
        observacoes = observacoes,
        flagCartaAnuencia = flagCartaAnuencia,
        dataCartaAnuencia = dataCartaAnuencia,
        flagPago = flagPago,
    )

    fun IngestBatchResult.toResponse(): IngestionResult = IngestionResult(
        batchId = batchId,
        totalReceived = totalReceived,
        totalSucceeded = totalSucceeded,
        totalFailed = totalFailed,
        results = results.map { it.toItemResponse() },
    )

    fun IngestItemResult.toItemResponse(): IngestionItemResult = IngestionItemResult(
        externalId = externalId,
        outcome = IngestionItemResult.Outcome.valueOf(outcome.name),
        errorMessage = errorMessage,
    )
}
