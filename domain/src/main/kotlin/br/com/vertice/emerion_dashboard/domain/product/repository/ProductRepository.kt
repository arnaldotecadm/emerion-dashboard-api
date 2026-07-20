package br.com.vertice.emerion_dashboard.domain.product.repository

import br.com.vertice.emerion_dashboard.domain.product.model.Product

/**
 * Outbound port (driven port) for Product persistence. Implemented by an
 * adapter in infrastructure/persistence/product (JPA today, but callers in
 * domain/application never know that). Keep this interface expressed purely
 * in domain terms.
 */
interface ProductRepository {
    fun findByExternalId(externalId: String): Product?

    /** Insert or update (by externalId) and return the persisted product. */
    fun save(product: Product): Product
}
