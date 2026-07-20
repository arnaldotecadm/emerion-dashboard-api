package br.com.vertice.emerion_dashboard.application.customer.ingestion

import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestCustomerCommand
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestItemResult

/**
 * Inbound port (driving port) for ingesting customer batches sent by
 * emerion-load-service. Implemented by IngestCustomersService.
 *
 * `ingestSingle` is a convenience for callers that send one record at a
 * time (e.g. the first version of emerion-load-service) — it is equivalent
 * to calling `ingest` with a single-item batch and unwrapping the one
 * result, so both call paths share the exact same upsert/idempotency logic.
 * Both methods are grouped in the same interface because they serve the
 * same functional concern (customer ingestion) — prefer one cohesive port
 * per domain concern over one interface per method.
 */
interface IngestCustomersUseCase {
    fun ingest(command: IngestBatchCommand): IngestBatchResult
    fun ingestSingle(command: IngestCustomerCommand): IngestItemResult
}
