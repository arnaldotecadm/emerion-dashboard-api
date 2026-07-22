package br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model

/** Input command for a batch of vendedores pushed by emerion-load-service. */
data class IngestBatchCommand(
    val batchId: String,
    val items: List<IngestVendedorCommand>,
)
