package br.com.vertice.emerion_dashboard.application.customer

import java.time.Instant

/** Input command for a single customer inside an ingestion batch. */
data class IngestCustomerCommand(
    val externalId: String,
    val nomeFantasia: String,
    val razaoSocial: String,
    val cpfCnpj: String,
    val inscricaoEstadual: String?,
    val regimeTributario: String?,
    val bloqueado: Boolean,
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
 *
 * `ingestSingle` is a convenience for callers that send one record at a
 * time (e.g. the first version of emerion-load-service) — it is equivalent
 * to calling `ingest` with a single-item batch and unwrapping the one
 * result, so both call paths share the exact same upsert/idempotency logic.
 */
interface IngestCustomersUseCase {
    fun ingest(command: IngestBatchCommand): IngestBatchResult
    fun ingestSingle(command: IngestCustomerCommand): IngestItemResult
}
