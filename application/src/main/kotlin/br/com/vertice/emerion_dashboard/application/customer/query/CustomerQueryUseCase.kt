package br.com.vertice.emerion_dashboard.application.customer.query

import br.com.vertice.emerion_dashboard.application.customer.query.model.ListCustomersQuery
import br.com.vertice.emerion_dashboard.domain.customer.model.Customer
import br.com.vertice.emerion_dashboard.domain.shared.Page

/**
 * Inbound port (driving port) for the customer read side, consumed by the
 * REST query adapter. `getById` and `list` are grouped in the same
 * interface because they serve the same functional concern (querying
 * customers) — prefer one cohesive port per domain concern over one
 * interface per method.
 */
interface CustomerQueryUseCase {
    fun getById(id: Long): Customer
    fun list(query: ListCustomersQuery): Page<Customer>
}
