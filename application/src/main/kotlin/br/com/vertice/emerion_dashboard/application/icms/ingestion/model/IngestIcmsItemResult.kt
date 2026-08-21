package br.com.vertice.emerion_dashboard.application.icms.ingestion.model

data class IngestIcmsItemResult(
    val externalId: String,
    val outcome: IngestIcmsOutcome,
    val errorMessage: String?,
)
