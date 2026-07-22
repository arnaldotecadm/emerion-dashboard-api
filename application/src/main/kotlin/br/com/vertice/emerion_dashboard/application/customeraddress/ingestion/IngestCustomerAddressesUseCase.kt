package br.com.vertice.emerion_dashboard.application.customeraddress.ingestion

import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestItemResult

/**
 * Inbound port (driving port) for ingesting customer address sets sent by
 * emerion-load-service. Implemented by IngestCustomerAddressesService.
 *
 * `ingestSingle` is a convenience for callers that send one record at a
 * time (this is the endpoint currently used by
 * emerion-load-service's CustomerAddressController#sendAddressToIngestion)
 * — it is equivalent to calling `ingest` with a single-item batch and
 * unwrapping the one result, so both call paths share the exact same
 * upsert/idempotency logic. Both methods are grouped in the same interface
 * because they serve the same functional concern (customer address
 * ingestion).
 */
interface IngestCustomerAddressesUseCase {
    fun ingest(command: IngestBatchCommand): IngestBatchResult
    fun ingestSingle(command: IngestCustomerAddressCommand): IngestItemResult
}
