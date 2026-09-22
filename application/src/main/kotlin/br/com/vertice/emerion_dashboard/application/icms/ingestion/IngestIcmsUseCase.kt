package br.com.vertice.emerion_dashboard.application.icms.ingestion

import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsBatchCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult

interface IngestIcmsUseCase {
    fun ingest(command: IngestIcmsBatchCommand): IngestBatchResult
    fun ingestSingle(command: IngestIcmsCommand): IngestItemResult
}
