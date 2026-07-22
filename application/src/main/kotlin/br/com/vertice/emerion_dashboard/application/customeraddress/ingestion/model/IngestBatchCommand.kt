package br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model

/** Input command for a batch of customer address sets pushed by emerion-load-service. */
data class IngestBatchCommand(
    val batchId: String,
    val items: List<IngestCustomerAddressCommand>,
)
