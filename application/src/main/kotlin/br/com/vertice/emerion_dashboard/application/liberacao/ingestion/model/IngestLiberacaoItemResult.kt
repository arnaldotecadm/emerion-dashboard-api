package br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model

import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome

data class IngestLiberacaoItemResult(
    val externalId: Long = 0L,
    val outcome: IngestOutcome,
    val errorMessage: String?,
    val externalIdString: String? = null,
)
