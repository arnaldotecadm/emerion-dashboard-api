package br.com.vertice.emerion_dashboard.application.product.ingestion

import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestProductCommand

/**
 * Inbound port (driving port) for ingesting product batches sent by
 * emerion-load-service. Implemented by IngestProductsService.
 *
 * `ingestSingle` is a convenience for callers that send one record at a
 * time (e.g. the first version of emerion-load-service) — it is equivalent
 * to calling `ingest` with a single-item batch and unwrapping the one
 * result, so both call paths share the exact same upsert/idempotency logic.
 * Both methods are grouped in the same interface because they serve the
 * same functional concern (product ingestion) — prefer one cohesive port
 * per domain concern over one interface per method.
 */
interface IngestProductsUseCase {
    fun ingest(command: IngestBatchCommand): IngestBatchResult
    fun ingestSingle(command: IngestProductCommand): IngestItemResult
}
