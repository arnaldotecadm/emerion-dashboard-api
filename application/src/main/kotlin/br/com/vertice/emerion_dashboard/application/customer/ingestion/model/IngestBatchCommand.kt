package br.com.vertice.emerion_dashboard.application.customer.ingestion.model

/** Input command for a batch of customers pushed by emerion-load-service. */
data class IngestBatchCommand(
    val batchId: String,
    val items: List<IngestCustomerCommand>,
)
