package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.ProductRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.product.ProductService
import br.com.vertice.emerion_dashboard.domain.product.ListProductsQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.ProductsApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController(
    private val productService: ProductService,
) : ProductsApi {

    override fun getProductById(id: Long): ResponseEntity<ProductResponse> {
        val product = productService.getById(id)
        return ResponseEntity.ok(product.toResponse())
    }

    override fun getProductByExternalId(externalId: String): ResponseEntity<ProductResponse> {
        val product = productService.getByExternalId(externalId)
        return ResponseEntity.ok(product.toResponse())
    }

    override fun listProducts(page: Int, size: Int, nome: String?, cnpjEmpresa: String?): ResponseEntity<ProductPage> {
        val query = ListProductsQuery(page = page, size = size, nomeContains = nome, cnpjEmpresa = cnpjEmpresa)
        val result = productService.list(query)
        return ResponseEntity.ok(
            ProductPage(
                data = result.content.map { it.toResponse() },
                pagination = PaginationInfo(
                    total = result.totalElements,
                    page = result.page,
                    propertySize = result.size,
                    totalPages = result.totalPages,
                ),
            ),
        )
    }
}
