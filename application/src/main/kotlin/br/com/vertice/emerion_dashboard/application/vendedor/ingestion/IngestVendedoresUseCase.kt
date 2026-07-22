package br.com.vertice.emerion_dashboard.application.vendedor.ingestion

import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestVendedorCommand

/**
 * Inbound port (driving port) for ingesting vendedor batches sent by
 * emerion-load-service. Implemented by IngestVendedoresService.
 *
 * `ingestSingle` is a convenience for callers that send one record at a
 * time (this is the endpoint currently used by emerion-load-service's
 * VendedorController#sendVendedorToIngestion) — it is equivalent to
 * calling `ingest` with a single-item batch and unwrapping the one result,
 * so both call paths share the exact same upsert/idempotency logic. Both
 * methods are grouped in the same interface because they serve the same
 * functional concern (vendedor ingestion) — prefer one cohesive port per
 * domain concern over one interface per method.
 */
interface IngestVendedoresUseCase {
    fun ingest(command: IngestBatchCommand): IngestBatchResult
    fun ingestSingle(command: IngestVendedorCommand): IngestItemResult
}
