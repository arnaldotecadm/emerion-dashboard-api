package br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model

/** Per-item outcome of an ingestion attempt. */
enum class IngestOutcome {
    CREATED,
    UPDATED,
    FAILED,
}
