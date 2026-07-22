package br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model

/** Input command for a batch of customer orders pushed by emerion-load-service. */
data class IngestBatchCommand(
    val batchId: String,
    val items: List<IngestCustomerOrderCommand>,
)
