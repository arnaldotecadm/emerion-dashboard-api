package br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model

import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome

data class IngestLiberacaoItemResult(
    val externalId: String,
    val outcome: IngestOutcome,
    val errorMessage: String?,
)
