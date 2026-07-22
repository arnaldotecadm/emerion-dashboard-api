package br.com.vertice.emerion_dashboard.domain.customerorder.repository

import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrder
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest

/**
 * Outbound port (driven port) for CustomerOrder persistence. Implemented by
 * an adapter in infrastructure/persistence/customerorder (JPA today, but
 * callers in domain/application never know that).
 */
interface CustomerOrderRepository {
    fun findById(id: Long): CustomerOrder?

    fun findByExternalId(externalId: String): CustomerOrder?

    fun findAll(
        pageRequest: PageRequest,
        codCli: String?,
        sitres: String?,
    ): Page<CustomerOrder>

    /** Insert or update (by externalId), replacing the itens list wholesale, and return the persisted order. */
    fun save(order: CustomerOrder): CustomerOrder
}
