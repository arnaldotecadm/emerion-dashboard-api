package br.com.vertice.emerion_dashboard.application.icms.ingestion.model

data class IngestIcmsBatchResult(val batchId: String, val results: List<IngestIcmsItemResult>) {
    val totalReceived: Int get() = results.size
    val totalSucceeded: Int get() = results.count { it.outcome != IngestIcmsOutcome.FAILED }
    val totalFailed: Int get() = results.count { it.outcome == IngestIcmsOutcome.FAILED }
}
