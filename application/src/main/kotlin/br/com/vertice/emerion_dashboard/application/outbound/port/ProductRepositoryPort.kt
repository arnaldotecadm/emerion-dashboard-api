package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.product.Product
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest

/**
 * Outbound port used by product application services to access persistence.
 */
interface ProductRepositoryPort {
    fun findById(id: Long): Product?

    fun findByExternalId(externalId: String): Product?

    fun findAll(
        pageRequest: PageRequest,
        nomeContains: String?,
        cnpjEmpresa: String?,
    ): Page<Product>

    fun save(product: Product): Product
}
