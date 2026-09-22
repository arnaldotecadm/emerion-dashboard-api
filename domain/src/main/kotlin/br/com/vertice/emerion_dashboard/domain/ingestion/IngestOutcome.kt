package br.com.vertice.emerion_dashboard.domain.ingestion

/** Per-item outcome of an ingestion attempt. */
enum class IngestOutcome {
    CREATED,
    UPDATED,
    FAILED,
    QUEUED
}
