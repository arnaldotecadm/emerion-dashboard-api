package br.com.vertice.emerion_dashboard.application.liberacao.ingestion

import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoCommand
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoItemResult

fun interface IngestLiberacaoUseCase {
    fun ingestSingle(command: IngestLiberacaoCommand): IngestLiberacaoItemResult
}
