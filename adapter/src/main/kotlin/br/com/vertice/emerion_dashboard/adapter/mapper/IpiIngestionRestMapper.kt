package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiBatchCommand
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IpiIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IpiIngestionItem

/** Maps between the generated ingestion DTOs and the application command/result models. */
object IpiIngestionRestMapper {

    fun IpiIngestionBatch.toCommand(): IngestIpiBatchCommand =
        IngestIpiBatchCommand(batchId, items.map { it.toItemCommand() })

    fun IpiIngestionItem.toItemCommand(): IngestIpiCommand = IngestIpiCommand(
        cnpjEmpresa = cnpjEmpresa,
        codigoIpi = codigoIpi,
        flgAtivo = flgAtivo,
        tipoIpi = tipoIpi,
        nomeIpi = nomeIpi,
        ncmIpi = ncmIpi,
        codigoEnquadramentoLegal = codigoEnquadramentoLegal,
        cstIpi = cstIpi,
        descricaoSituacaoTributariaIpi = descricaoSituacaoTributariaIpi,
        aliquotaIpi = aliquotaIpi,
        percentualBaseCalculoIpi = percentualBaseCalculoIpi,
        flgSineif20 = flgSineif20,
        codigoTextoFiscal = codigoTextoFiscal,
        cstPis = cstPis,
        descricaoSituacaoTributariaPis = descricaoSituacaoTributariaPis,
        aliquotaPis = aliquotaPis,
        incluiDescontoSuframaPis = incluiDescontoSuframaPis,
        cstCofins = cstCofins,
        descricaoSituacaoTributariaCofins = descricaoSituacaoTributariaCofins,
        aliquotaCofins = aliquotaCofins,
        incluiDescontoSuframaCofins = incluiDescontoSuframaCofins,
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
