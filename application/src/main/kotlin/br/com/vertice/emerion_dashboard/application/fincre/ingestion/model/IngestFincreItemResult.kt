package br.com.vertice.emerion_dashboard.application.fincre.ingestion.model

data class IngestFincreItemResult(
    val externalId: String,
    val outcome: IngestFincreOutcome,
    val errorMessage: String?,
)
