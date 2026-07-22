package br.com.vertice.emerion_dashboard.application.customerorder.ingestion

import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestItemResult

/**
 * Inbound port (driving port) for ingesting customer order batches sent by
 * emerion-load-service. Implemented by IngestCustomerOrdersService.
 *
 * `ingestSingle` is a convenience for callers that send one record at a
 * time (this is the endpoint currently used by
 * emerion-load-service's CustomerOrderController#sendOrderToIngestion) — it
 * is equivalent to calling `ingest` with a single-item batch and
 * unwrapping the one result, so both call paths share the exact same
 * upsert/idempotency logic. Both methods are grouped in the same interface
 * because they serve the same functional concern (customer order
 * ingestion).
 */
interface IngestCustomerOrdersUseCase {
    fun ingest(command: IngestBatchCommand): IngestBatchResult
    fun ingestSingle(command: IngestCustomerOrderCommand): IngestItemResult
}
