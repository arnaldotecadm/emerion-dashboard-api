package br.com.vertice.emerion_dashboard.application.customercredit.ingestion

import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestCustomerCreditCommand
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestItemResult

/**
 * Inbound port (driving port) for ingesting customer credit ledger entries
 * sent by emerion-load-service. Implemented by IngestCustomerCreditsService.
 *
 * `ingest` backs the plain-array `/ingestion/customer-credits/batch`
 * endpoint (the one emerion-load-service's
 * CustomerCreditController#sendCreditsToIngestion currently calls, via
 * CustomerCreditService#sendCreditsToIngestion) and `ingestSingle` backs
 * `/ingestion/customer-credits/single`, for parity with the other
 * ingestion resources. Both share the exact same upsert logic.
 */
interface IngestCustomerCreditsUseCase {
    fun ingest(command: IngestBatchCommand): IngestBatchResult
    fun ingestSingle(command: IngestCustomerCreditCommand): IngestItemResult
}
