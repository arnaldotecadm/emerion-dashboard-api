package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsBatchCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IcmsIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IcmsIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

/** Maps between the generated ingestion DTOs and the application command/result models. */
object IcmsIngestionRestMapper {

    fun IcmsIngestionBatch.toCommand(): IngestIcmsBatchCommand =
        IngestIcmsBatchCommand(batchId, items.map { it.toItemCommand() })

    fun IcmsIngestionItem.toItemCommand(): IngestIcmsCommand = IngestIcmsCommand(
        cnpjEmpresa = cnpjEmpresa,
        codigoIcms = codigoIcms,
        tipoIcms = tipoIcms,
        nomeIcms = nomeIcms,
        ufEmitente = ufEmitente,
        codigoRegimeTributario = codigoRegimeTributario,
        aliquotaIcms = aliquotaIcms,
        percentualReducaoValorImposto = percentualReducaoValorImposto,
        percentualBaseCalculoIcms = percentualBaseCalculoIcms,
        situacaoTributariaIcms = situacaoTributariaIcms,
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
