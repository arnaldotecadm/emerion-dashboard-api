package br.com.vertice.emerion_dashboard.application.ipi.ingestion

import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiBatchCommand
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult

interface IngestIpiUseCase {
    fun ingest(command: IngestIpiBatchCommand): IngestBatchResult
    fun ingestSingle(command: IngestIpiCommand): IngestItemResult
}
