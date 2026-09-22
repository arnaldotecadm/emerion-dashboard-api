package br.com.vertice.emerion_dashboard.domain.ingestion

/** Result of ingesting a single vendedor item, whether standalone or as part of a batch. */
data class IngestItemResult(
    val externalId: String,
    val outcome: IngestOutcome,
    val errorMessage: String?,
)
