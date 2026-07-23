package br.com.vertice.emerion_dashboard.application.product.query

import br.com.vertice.emerion_dashboard.application.product.query.model.ListProductsQuery
import br.com.vertice.emerion_dashboard.domain.product.model.Product
import br.com.vertice.emerion_dashboard.domain.shared.Page

/**
 * Inbound port (driving port) for the product read side, consumed by the
 * REST query adapter. `getById` and `list` are grouped in the same
 * interface because they serve the same functional concern (querying
 * products) — prefer one cohesive port per domain concern over one
 * interface per method.
 */
interface ProductQueryUseCase {
    fun getById(id: Long): Product
    fun getByExternalId(externalId: String): Product
    fun list(query: ListProductsQuery): Page<Product>
}
