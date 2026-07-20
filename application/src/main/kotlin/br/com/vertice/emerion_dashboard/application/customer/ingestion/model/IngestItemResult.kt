package br.com.vertice.emerion_dashboard.application.customer.ingestion.model

/** Result of ingesting a single customer item, whether standalone or as part of a batch. */
data class IngestItemResult(
    val externalId: String,
    val outcome: IngestOutcome,
    val errorMessage: String?,
)
