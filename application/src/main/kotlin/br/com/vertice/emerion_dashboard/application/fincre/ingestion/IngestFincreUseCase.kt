package br.com.vertice.emerion_dashboard.application.fincre.ingestion

import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreBatchCommand
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult

interface IngestFincreUseCase {
    fun ingest(command: IngestFincreBatchCommand): IngestBatchResult
    fun ingestSingle(command: IngestFincreCommand): IngestItemResult
}
