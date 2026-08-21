package br.com.vertice.emerion_dashboard.application.fincre.ingestion

import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreBatchCommand
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreBatchResult
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreCommand
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreItemResult

interface IngestFincreUseCase {
    fun ingest(command: IngestFincreBatchCommand): IngestFincreBatchResult
    fun ingestSingle(command: IngestFincreCommand): IngestFincreItemResult
}
