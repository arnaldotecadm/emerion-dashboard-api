package br.com.vertice.emerion_dashboard.application.ipi.ingestion.model

data class IngestIpiItemResult(
    val externalId: String,
    val outcome: IngestIpiOutcome,
    val errorMessage: String?,
)
