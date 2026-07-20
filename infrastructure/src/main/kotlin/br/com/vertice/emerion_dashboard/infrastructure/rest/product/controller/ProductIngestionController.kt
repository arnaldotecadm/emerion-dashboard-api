package br.com.vertice.emerion_dashboard.infrastructure.rest.product.controller

import br.com.vertice.emerion_dashboard.application.product.ingestion.IngestProductsUseCase
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.ProductIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.product.mapper.ProductIngestionRestMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter: implements the generated `ProductIngestionApi`
 * contract and translates HTTP <-> application layer. Contains no business
 * logic — that lives in `IngestProductsUseCase`.
 */
@RestController
class ProductIngestionController(
    private val ingestProductsUseCase: IngestProductsUseCase,
) : ProductIngestionApi {

    override fun ingestProducts(productIngestionBatch: ProductIngestionBatch): ResponseEntity<IngestionResult> {
        val command = ProductIngestionRestMapper.toCommand(productIngestionBatch)
        val result = ingestProductsUseCase.ingest(command)
        return ResponseEntity.ok(ProductIngestionRestMapper.toResponse(result))
    }

    override fun ingestSingleProduct(productIngestionItem: ProductIngestionItem): ResponseEntity<IngestionItemResult> {
        val command = ProductIngestionRestMapper.toItemCommand(productIngestionItem)
        val result = ingestProductsUseCase.ingestSingle(command)
        return ResponseEntity.ok(ProductIngestionRestMapper.toItemResponse(result))
    }
}
