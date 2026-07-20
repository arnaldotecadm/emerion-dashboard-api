package br.com.vertice.emerion_dashboard.application.customer.ingestion.model

/** Per-item outcome of an ingestion attempt. */
enum class IngestOutcome {
    CREATED,
    UPDATED,
    FAILED,
}
