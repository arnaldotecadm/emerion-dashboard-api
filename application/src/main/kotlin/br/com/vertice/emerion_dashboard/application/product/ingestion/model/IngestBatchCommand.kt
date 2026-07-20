package br.com.vertice.emerion_dashboard.application.product.ingestion.model

/** Input command for a batch of products pushed by emerion-load-service. */
data class IngestBatchCommand(
    val batchId: String,
    val items: List<IngestProductCommand>,
)
