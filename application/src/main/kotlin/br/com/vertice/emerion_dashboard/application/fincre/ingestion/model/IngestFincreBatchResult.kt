package br.com.vertice.emerion_dashboard.application.fincre.ingestion.model

data class IngestFincreBatchResult(val batchId: String, val results: List<IngestFincreItemResult>) {
    val totalReceived: Int get() = results.size
    val totalSucceeded: Int get() = results.count { it.outcome != IngestFincreOutcome.FAILED }
    val totalFailed: Int get() = results.count { it.outcome == IngestFincreOutcome.FAILED }
}
