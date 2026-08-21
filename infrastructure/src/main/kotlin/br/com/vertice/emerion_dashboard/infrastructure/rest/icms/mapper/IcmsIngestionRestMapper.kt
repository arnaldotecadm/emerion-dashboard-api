package br.com.vertice.emerion_dashboard.infrastructure.rest.icms.mapper

import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsBatchCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsBatchResult
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IcmsIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IcmsIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

object IcmsIngestionRestMapper {
    fun toCommand(dto: IcmsIngestionBatch) = IngestIcmsBatchCommand(dto.batchId, dto.items.map(::toItemCommand))

    fun toItemCommand(dto: IcmsIngestionItem) = IngestIcmsCommand(
        cnpjEmpresa = dto.cnpjEmpresa,
        codigoIcms = dto.codigoIcms,
        tipoIcms = dto.tipoIcms,
        nomeIcms = dto.nomeIcms,
        ufEmitente = dto.ufEmitente,
        codigoRegimeTributario = dto.codigoRegimeTributario,
        aliquotaIcms = dto.aliquotaIcms,
        percentualReducaoValorImposto = dto.percentualReducaoValorImposto,
        percentualBaseCalculoIcms = dto.percentualBaseCalculoIcms,
        situacaoTributariaIcms = dto.situacaoTributariaIcms,
    )

    fun toResponse(result: IngestIcmsBatchResult) = IngestionResult(
        batchId = result.batchId,
        totalReceived = result.totalReceived,
        totalSucceeded = result.totalSucceeded,
        totalFailed = result.totalFailed,
        results = result.results.map(::toItemResponse),
    )

    fun toItemResponse(result: IngestIcmsItemResult) = IngestionItemResult(
        externalId = result.externalId,
        outcome = IngestionItemResult.Outcome.valueOf(result.outcome.name),
        errorMessage = result.errorMessage,
    )
}
