package br.com.vertice.emerion_dashboard.application.vendedor.query

import br.com.vertice.emerion_dashboard.application.vendedor.query.model.ListVendedoresQuery
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.vendedor.model.Vendedor

/**
 * Inbound port (driving port) for the vendedor read side, consumed by the
 * REST query adapter. `getById` and `list` are grouped in the same
 * interface because they serve the same functional concern (querying
 * vendedores) — prefer one cohesive port per domain concern over one
 * interface per method.
 */
interface VendedorQueryUseCase {
    fun getById(id: Long): Vendedor
    fun list(query: ListVendedoresQuery): Page<Vendedor>
}
