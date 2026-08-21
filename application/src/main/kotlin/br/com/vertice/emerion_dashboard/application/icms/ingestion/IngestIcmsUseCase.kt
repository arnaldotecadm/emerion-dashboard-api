package br.com.vertice.emerion_dashboard.application.icms.ingestion

import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsBatchCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsBatchResult
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsItemResult

interface IngestIcmsUseCase {
    fun ingest(command: IngestIcmsBatchCommand): IngestIcmsBatchResult
    fun ingestSingle(command: IngestIcmsCommand): IngestIcmsItemResult
}
