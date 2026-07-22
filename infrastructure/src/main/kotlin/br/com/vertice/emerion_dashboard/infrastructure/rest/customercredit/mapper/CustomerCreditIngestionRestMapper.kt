package br.com.vertice.emerion_dashboard.infrastructure.rest.customercredit.mapper

import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestCustomerCreditCommand
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerCreditIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import java.util.UUID

/** Maps between the generated OpenAPI DTOs and the application layer's use-case commands/results. */
object CustomerCreditIngestionRestMapper {

    /**
     * Builds a batch command from the bare array emerion-load-service posts
     * (no batchId envelope). A batchId is generated here purely for
     * tracing/logging and the response's `batchId` field.
     */
    fun toCommand(dtos: List<CustomerCreditIngestionItem>): IngestBatchCommand =
        IngestBatchCommand(
            batchId = UUID.randomUUID().toString(),
            items = dtos.map(::toItemCommand),
        )

    fun toItemCommand(dto: CustomerCreditIngestionItem): IngestCustomerCreditCommand =
        IngestCustomerCreditCommand(
            customerExternalId = dto.customerExternalId,
            sequencia = dto.sequencia,
            data = dto.data.toInstant(),
            dataPedido = dto.dataPedido?.toInstant(),
            valorUtilizado = dto.valorUtilizado,
            valorTotal = dto.valorTotal,
            saldo = dto.saldo,
            situacao = dto.situacao,
            tipo = dto.tipo,
        )

    fun toResponse(result: IngestBatchResult): IngestionResult =
        IngestionResult(
            batchId = result.batchId,
            totalReceived = result.totalReceived,
            totalSucceeded = result.totalSucceeded,
            totalFailed = result.totalFailed,
            results = result.results.map(::toItemResponse),
        )

    fun toItemResponse(result: IngestItemResult): IngestionItemResult =
        IngestionItemResult(
            externalId = result.externalId,
            outcome = IngestionItemResult.Outcome.valueOf(result.outcome.name),
            errorMessage = result.errorMessage,
        )
}
