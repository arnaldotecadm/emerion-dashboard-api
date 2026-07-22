package br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model

/**
 * Result of ingesting a single customer credit entry, whether standalone
 * or as part of a batch. `externalId` here is the (customerExternalId,
 * sequencia) pair rendered for tracing purposes; see the REST mapper.
 */
data class IngestItemResult(
    val externalId: String,
    val outcome: IngestOutcome,
    val errorMessage: String?,
)
