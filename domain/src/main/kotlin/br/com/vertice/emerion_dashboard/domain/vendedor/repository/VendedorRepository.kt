package br.com.vertice.emerion_dashboard.domain.vendedor.repository

import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.domain.vendedor.model.Vendedor

/**
 * Outbound port (driven port) for Vendedor persistence. Implemented by an
 * adapter in infrastructure/persistence/vendedor (JPA today, but callers in
 * domain/application never know that). Keep this interface expressed purely
 * in domain terms.
 */
interface VendedorRepository {
    fun findById(id: Long): Vendedor?

    fun findByExternalId(externalId: String): Vendedor?

    fun findAll(
        pageRequest: PageRequest,
        nomeContains: String?,
        situacao: String?,
    ): Page<Vendedor>

    /** Insert or update (by externalId) and return the persisted vendedor. */
    fun save(vendedor: Vendedor): Vendedor
}
