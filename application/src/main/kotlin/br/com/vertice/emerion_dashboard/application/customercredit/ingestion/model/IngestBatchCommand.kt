package br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model

/** Input command for a batch of customer credit entries pushed by emerion-load-service. */
data class IngestBatchCommand(
    val batchId: String,
    val items: List<IngestCustomerCreditCommand>,
)
