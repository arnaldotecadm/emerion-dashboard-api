package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.ProductIngestionRestMapper.toCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.ProductIngestionRestMapper.toItemCommand
import br.com.vertice.emerion_dashboard.adapter.mapper.ProductIngestionRestMapper.toItemResponse
import br.com.vertice.emerion_dashboard.adapter.mapper.ProductIngestionRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.product.ingestion.IngestProductsService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.ProductIngestionApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductIngestionItem
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductIngestionController(
    private val ingestProductsService: IngestProductsService,
) : ProductIngestionApi {

    override fun ingestProducts(productIngestionBatch: ProductIngestionBatch): ResponseEntity<IngestionResult> =
        ResponseEntity.ok(ingestProductsService.ingest(productIngestionBatch.toCommand()).toResponse())

    override fun ingestSingleProduct(productIngestionItem: ProductIngestionItem): ResponseEntity<IngestionItemResult> =
        ResponseEntity.ok(ingestProductsService.ingestSingle(productIngestionItem.toItemCommand()).toItemResponse())
}
