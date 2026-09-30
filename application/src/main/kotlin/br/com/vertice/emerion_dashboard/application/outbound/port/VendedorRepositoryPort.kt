package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.domain.vendedor.Vendedor

/**
 * Outbound port used by vendedor application services to access persistence.
 */
interface VendedorRepositoryPort {
    fun findById(id: Long): Vendedor?

    fun findByExternalId(externalId: String): Vendedor?

    fun findAll(
        pageRequest: PageRequest,
        nomeContains: String?,
        ativo: String?,
        cnpjEmpresa: String?,
    ): Page<Vendedor>

    fun save(vendedor: Vendedor): Vendedor
}
