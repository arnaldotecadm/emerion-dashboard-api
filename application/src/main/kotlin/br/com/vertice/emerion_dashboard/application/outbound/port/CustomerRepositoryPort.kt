package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest

/**
 * Outbound port used by customer application services to access persistence.
 */
interface CustomerRepositoryPort {
    fun findById(id: Long): Customer?

    fun findByExternalId(externalId: String): Customer?

    fun findAll(
        pageRequest: PageRequest,
        bloqueado: Boolean?,
        nomeFantasiaContains: String?,
        cnpjEmpresa: String?,
    ): Page<Customer>

    fun save(customer: Customer): Customer
}
