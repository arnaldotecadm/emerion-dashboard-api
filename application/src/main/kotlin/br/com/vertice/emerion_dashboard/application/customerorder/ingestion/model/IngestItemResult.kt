package br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model

/** Result of ingesting a single customer order, whether standalone or as part of a batch. */
data class IngestItemResult(
    val externalId: String,
    val outcome: IngestOutcome,
    val errorMessage: String?,
)
