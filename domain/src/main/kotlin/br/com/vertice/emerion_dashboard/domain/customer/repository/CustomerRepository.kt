package br.com.vertice.emerion_dashboard.domain.customer.repository

import br.com.vertice.emerion_dashboard.domain.customer.model.Customer
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest

/**
 * Outbound port (driven port) for Customer persistence. Implemented by an
 * adapter in infrastructure/persistence/customer (JPA today, but callers in
 * domain/application never know that). Keep this interface expressed purely
 * in domain terms.
 */
interface CustomerRepository {
    fun findById(id: Long): Customer?

    fun findByExternalId(externalId: String): Customer?

    fun findAll(
        pageRequest: PageRequest,
        bloqueado: Boolean?,
        nomeFantasiaContains: String?,
        cnpjEmpresa: String?,
    ): Page<Customer>

    /** Insert or update (by externalId) and return the persisted customer. */
    fun save(customer: Customer): Customer
}
