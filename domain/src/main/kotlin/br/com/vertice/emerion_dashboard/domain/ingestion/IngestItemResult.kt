package br.com.vertice.emerion_dashboard.domain.ingestion

/** Result of ingesting a single vendedor item, whether standalone or as part of a batch. */
data class IngestItemResult(
    val externalId: Long = 0L,
    val outcome: IngestOutcome,
    val errorMessage: String?,
    val externalIdString: String? = null,
)
