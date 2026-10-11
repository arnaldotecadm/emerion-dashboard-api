package br.com.vertice.emerion_dashboard.application.customerquotation.ingestion.model

data class IngestCustomerQuotationBatchCommand(
    val batchId: String,
    val items: List<IngestCustomerQuotationCommand>,
)
