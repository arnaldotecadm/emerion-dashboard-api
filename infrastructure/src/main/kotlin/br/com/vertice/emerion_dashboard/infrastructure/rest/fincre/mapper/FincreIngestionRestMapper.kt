package br.com.vertice.emerion_dashboard.infrastructure.rest.fincre.mapper

import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreBatchCommand
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreBatchResult
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreCommand
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreItemResult
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreParcelaCommand
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FincreIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FincreIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FincreParcelaIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

object FincreIngestionRestMapper {
    fun toCommand(dto: FincreIngestionBatch) = IngestFincreBatchCommand(dto.batchId, dto.items.map(::toItemCommand))

    fun toItemCommand(dto: FincreIngestionItem) = IngestFincreCommand(
        cnpjEmpresa = dto.cnpjEmpresa,
        codigoEmpresa = dto.codigoEmpresa,
        dataEmissao = dto.dataEmissao,
        documento = dto.documento,
        codigoCondicaoRecebimento = dto.codigoCondicaoRecebimento,
        nomeCondicaoRecebimento = dto.nomeCondicaoRecebimento,
        nomeEmpresa = dto.nomeEmpresa,
        codigoComissao = dto.codigoComissao,
        percentualComissao = dto.percentualComissao,
        codigoCliente = dto.codigoCliente,
        nomeCliente = dto.nomeCliente,
        codigoVendedor = dto.codigoVendedor,
        nomeVendedor = dto.nomeVendedor,
        codigoTipoDocumento = dto.codigoTipoDocumento,
        nomeTipoDocumento = dto.nomeTipoDocumento,
        parcelas = dto.parcelas.map(::toParcelaCommand),
    )

    private fun toParcelaCommand(dto: FincreParcelaIngestionItem) = IngestFincreParcelaCommand(
        numeroParcela = dto.numeroParcela,
        flagIncobravel = dto.flagIncobravel,
        dataIncobravel = dto.dataIncobravel,
        dataVencimento = dto.dataVencimento,
        prazoEmDias = dto.prazoEmDias,
        valorParcela = dto.valorParcela,
        numeroBancario = dto.numeroBancario,
        codigoBanco = dto.codigoBanco,
        nomeBanco = dto.nomeBanco,
        observacoes = dto.observacoes,
        flagCartaAnuencia = dto.flagCartaAnuencia,
        dataCartaAnuencia = dto.dataCartaAnuencia,
        flagPago = dto.flagPago,
    )

    fun toResponse(result: IngestFincreBatchResult) = IngestionResult(
        batchId = result.batchId,
        totalReceived = result.totalReceived,
        totalSucceeded = result.totalSucceeded,
        totalFailed = result.totalFailed,
        results = result.results.map(::toItemResponse),
    )

    fun toItemResponse(result: IngestFincreItemResult) = IngestionItemResult(
        externalId = result.externalId,
        outcome = IngestionItemResult.Outcome.valueOf(result.outcome.name),
        errorMessage = result.errorMessage,
    )
}
