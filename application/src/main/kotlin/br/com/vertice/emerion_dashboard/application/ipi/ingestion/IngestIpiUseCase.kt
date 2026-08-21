package br.com.vertice.emerion_dashboard.application.ipi.ingestion

import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiBatchCommand
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiBatchResult
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiCommand
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiItemResult

interface IngestIpiUseCase {
    fun ingest(command: IngestIpiBatchCommand): IngestIpiBatchResult
    fun ingestSingle(command: IngestIpiCommand): IngestIpiItemResult
}
