package br.com.vertice.emerion_dashboard.infrastructure.rest.product.controller

import br.com.vertice.emerion_dashboard.application.product.query.ProductQueryUseCase
import br.com.vertice.emerion_dashboard.application.product.query.model.ListProductsQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.ProductsApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.product.mapper.ProductQueryRestMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter for the product read endpoints consumed by the
 * React frontend. Implements the generated `ProductsApi` contract, contains
 * no business logic.
 */
@RestController
class ProductQueryController(
    private val productQueryUseCase: ProductQueryUseCase,
) : ProductsApi {

    override fun getProductById(id: Long): ResponseEntity<ProductResponse> {
        val product = productQueryUseCase.getById(id)
        return ResponseEntity.ok(ProductQueryRestMapper.toResponse(product))
    }

    override fun getProductByExternalId(externalId: String): ResponseEntity<ProductResponse> {
        val product = productQueryUseCase.getByExternalId(externalId)
        return ResponseEntity.ok(ProductQueryRestMapper.toResponse(product))
    }

    override fun listProducts(page: Int, size: Int, nome: String?): ResponseEntity<ProductPage> {
        val query = ListProductsQuery(page = page, size = size, nomeContains = nome)
        val result = productQueryUseCase.list(query)
        return ResponseEntity.ok(ProductQueryRestMapper.toPageResponse(result))
    }
}
