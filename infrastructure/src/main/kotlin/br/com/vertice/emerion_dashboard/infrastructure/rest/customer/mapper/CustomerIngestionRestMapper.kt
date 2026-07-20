package br.com.vertice.emerion_dashboard.infrastructure.rest.customer.mapper

import br.com.vertice.emerion_dashboard.application.customer.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customer.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customer.IngestCustomerCommand
import br.com.vertice.emerion_dashboard.application.customer.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

/** Maps between the generated OpenAPI DTOs and the application layer's use-case commands/results. */
object CustomerIngestionRestMapper {

    fun toCommand(dto: CustomerIngestionBatch): IngestBatchCommand =
        IngestBatchCommand(
            batchId = dto.batchId,
            items = dto.items.map(::toItemCommand),
        )

    fun toItemCommand(dto: CustomerIngestionItem): IngestCustomerCommand =
        IngestCustomerCommand(
            externalId = dto.externalId,
            nomeFantasia = dto.nomeFantasia,
            razaoSocial = dto.razaoSocial,
            cpfCnpj = dto.cpfCnpj,
            inscricaoEstadual = dto.inscricaoEstadual,
            regimeTributario = dto.regimeTributario,
            bloqueado = dto.bloqueado,
            createdAt = dto.createdAt?.toInstant(),
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
