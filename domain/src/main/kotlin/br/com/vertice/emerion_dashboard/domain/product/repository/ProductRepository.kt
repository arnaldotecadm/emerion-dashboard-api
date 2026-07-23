package br.com.vertice.emerion_dashboard.domain.product.repository

import br.com.vertice.emerion_dashboard.domain.product.model.Product
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest

/**
 * Outbound port (driven port) for Product persistence. Implemented by an
 * adapter in infrastructure/persistence/product (JPA today, but callers in
 * domain/application never know that). Keep this interface expressed purely
 * in domain terms.
 */
interface ProductRepository {
    fun findById(id: Long): Product?

    fun findByExternalId(externalId: String): Product?

    fun findAll(
        pageRequest: PageRequest,
        nomeContains: String?,
        cnpjEmpresa: String?,
    ): Page<Product>

    /** Insert or update (by externalId) and return the persisted product. */
    fun save(product: Product): Product
}
