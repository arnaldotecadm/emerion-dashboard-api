package br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model

/** Result of ingesting a single customer address set, whether standalone or as part of a batch. */
data class IngestItemResult(
    val externalId: String,
    val outcome: IngestOutcome,
    val errorMessage: String?,
)
