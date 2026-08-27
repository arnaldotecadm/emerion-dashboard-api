package br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model

data class IngestLiberacaoItemResult(
    val externalId: String,
    val outcome: IngestLiberacaoOutcome,
    val errorMessage: String?,
)
