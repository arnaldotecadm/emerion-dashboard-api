package br.com.vertice.emerion_dashboard.infrastructure.rest.product.mapper

import br.com.vertice.emerion_dashboard.domain.product.model.Product
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductResponse
import java.time.ZoneOffset

/** Maps between the domain model and the generated OpenAPI query DTOs. */
object ProductQueryRestMapper {

    fun toResponse(product: Product): ProductResponse =
        ProductResponse(
            id = product.id,
            externalId = product.externalId,
            nome = product.nome,
            preco = product.preco,
            createdAt = product.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = product.updatedAt.atOffset(ZoneOffset.UTC),
        )

    fun toPageResponse(page: DomainPage<Product>): ProductPage =
        ProductPage(
            data = page.content.map(::toResponse),
            pagination = PaginationInfo(
                total = page.totalElements,
                page = page.page,
                propertySize = page.size,
                totalPages = page.totalPages,
            ),
        )
}
