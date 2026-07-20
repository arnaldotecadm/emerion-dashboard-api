package br.com.vertice.emerion_dashboard.infrastructure.rest.customer

import br.com.vertice.emerion_dashboard.application.customer.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customer.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customer.IngestCustomerCommand
import br.com.vertice.emerion_dashboard.application.customer.IngestOutcome
import br.com.vertice.emerion_dashboard.domain.customer.CustomerStatus as DomainCustomerStatus
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerStatus as ApiCustomerStatus
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

/** Maps between the generated OpenAPI DTOs and the application layer's use-case commands/results. */
object CustomerIngestionRestMapper {

    fun toCommand(dto: CustomerIngestionBatch): IngestBatchCommand =
        IngestBatchCommand(
            batchId = dto.batchId,
            items = dto.items.map(::toItemCommand),
        )

    private fun toItemCommand(dto: CustomerIngestionItem): IngestCustomerCommand =
        IngestCustomerCommand(
            externalId = dto.externalId,
            name = dto.name,
            email = dto.email,
            status = toDomainStatus(dto.status),
            createdAt = dto.createdAt?.toInstant(),
        )

    fun toResponse(result: IngestBatchResult): IngestionResult =
        IngestionResult(
            batchId = result.batchId,
            totalReceived = result.totalReceived,
            totalSucceeded = result.totalSucceeded,
            totalFailed = result.totalFailed,
            results = result.results.map {
                IngestionItemResult(
                    externalId = it.externalId,
                    outcome = IngestionItemResult.Outcome.valueOf(it.outcome.name),
                    errorMessage = it.errorMessage,
                )
            },
        )

    private fun toDomainStatus(status: ApiCustomerStatus?): DomainCustomerStatus =
        when (status) {
            ApiCustomerStatus.ACTIVE -> DomainCustomerStatus.ACTIVE
            ApiCustomerStatus.INACTIVE -> DomainCustomerStatus.INACTIVE
            ApiCustomerStatus.UNKNOWN, null -> DomainCustomerStatus.UNKNOWN
        }
}
