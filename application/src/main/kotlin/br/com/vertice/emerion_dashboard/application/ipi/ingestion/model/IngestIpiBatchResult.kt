package br.com.vertice.emerion_dashboard.application.ipi.ingestion.model

data class IngestIpiBatchResult(val batchId: String, val results: List<IngestIpiItemResult>) {
    val totalReceived: Int get() = results.size
    val totalSucceeded: Int get() = results.count { it.outcome != IngestIpiOutcome.FAILED }
    val totalFailed: Int get() = results.count { it.outcome == IngestIpiOutcome.FAILED }
}
