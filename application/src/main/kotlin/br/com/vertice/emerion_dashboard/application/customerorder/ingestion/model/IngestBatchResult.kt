package br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model

/** Result of ingesting a batch of customer orders, aggregating per-item outcomes. */
data class IngestBatchResult(
    val batchId: String,
    val results: List<IngestItemResult>,
) {
    val totalReceived: Int get() = results.size
    val totalSucceeded: Int get() = results.count { it.outcome != IngestOutcome.FAILED }
    val totalFailed: Int get() = results.count { it.outcome == IngestOutcome.FAILED }
}
