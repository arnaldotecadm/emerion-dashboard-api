package br.com.vertice.emerion_dashboard.application.customer

import br.com.vertice.emerion_dashboard.domain.customer.CustomerStatus
import java.time.Instant

/** Input command for a single customer inside an ingestion batch. */
data class IngestCustomerCommand(
    val externalId: String,
    val name: String,
    val email: String?,
    val status: CustomerStatus,
    val createdAt: Instant?,
)

data class IngestBatchCommand(
    val batchId: String,
    val items: List<IngestCustomerCommand>,
)

enum class IngestOutcome {
    CREATED,
    UPDATED,
    FAILED,
}

data class IngestItemResult(
    val externalId: String,
    val outcome: IngestOutcome,
    val errorMessage: String?,
)

data class IngestBatchResult(
    val batchId: String,
    val results: List<IngestItemResult>,
) {
    val totalReceived: Int get() = results.size
    val totalSucceeded: Int get() = results.count { it.outcome != IngestOutcome.FAILED }
    val totalFailed: Int get() = results.count { it.outcome == IngestOutcome.FAILED }
}

/**
 * Inbound port (driving port) for ingesting customer batches sent by
 * emerion-load-service. Implemented by IngestCustomersService.
 */
fun interface IngestCustomersUseCase {
    fun ingest(command: IngestBatchCommand): IngestBatchResult
}
