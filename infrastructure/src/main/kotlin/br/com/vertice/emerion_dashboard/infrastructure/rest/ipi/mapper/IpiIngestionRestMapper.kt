package br.com.vertice.emerion_dashboard.infrastructure.rest.ipi.mapper

import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiBatchCommand
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiBatchResult
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiCommand
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IpiIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IpiIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

object IpiIngestionRestMapper {
    fun toCommand(dto: IpiIngestionBatch) = IngestIpiBatchCommand(dto.batchId, dto.items.map(::toItemCommand))

    fun toItemCommand(dto: IpiIngestionItem) = IngestIpiCommand(
        cnpjEmpresa = dto.cnpjEmpresa,
        codigoIpi = dto.codigoIpi,
        flgAtivo = dto.flgAtivo,
        tipoIpi = dto.tipoIpi,
        nomeIpi = dto.nomeIpi,
        ncmIpi = dto.ncmIpi,
        codigoEnquadramentoLegal = dto.codigoEnquadramentoLegal,
        cstIpi = dto.cstIpi,
        descricaoSituacaoTributariaIpi = dto.descricaoSituacaoTributariaIpi,
        aliquotaIpi = dto.aliquotaIpi,
        percentualBaseCalculoIpi = dto.percentualBaseCalculoIpi,
        flgSineif20 = dto.flgSineif20,
        codigoTextoFiscal = dto.codigoTextoFiscal,
        cstPis = dto.cstPis,
        descricaoSituacaoTributariaPis = dto.descricaoSituacaoTributariaPis,
        aliquotaPis = dto.aliquotaPis,
        incluiDescontoSuframaPis = dto.incluiDescontoSuframaPis,
        cstCofins = dto.cstCofins,
        descricaoSituacaoTributariaCofins = dto.descricaoSituacaoTributariaCofins,
        aliquotaCofins = dto.aliquotaCofins,
        incluiDescontoSuframaCofins = dto.incluiDescontoSuframaCofins,
    )

    fun toResponse(result: IngestIpiBatchResult) = IngestionResult(
        batchId = result.batchId,
        totalReceived = result.totalReceived,
        totalSucceeded = result.totalSucceeded,
        totalFailed = result.totalFailed,
        results = result.results.map(::toItemResponse),
    )

    fun toItemResponse(result: IngestIpiItemResult) = IngestionItemResult(
        externalId = result.externalId,
        outcome = IngestionItemResult.Outcome.valueOf(result.outcome.name),
        errorMessage = result.errorMessage,
    )
}
