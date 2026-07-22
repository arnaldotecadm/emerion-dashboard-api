package br.com.vertice.emerion_dashboard.application.customerorder.query

import br.com.vertice.emerion_dashboard.application.customerorder.query.model.ListCustomerOrdersQuery
import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrder
import br.com.vertice.emerion_dashboard.domain.shared.Page

/**
 * Inbound port (driving port) for the customer order read side, consumed by
 * the REST query adapter. `getById` and `list` are grouped in the same
 * interface because they serve the same functional concern (querying
 * customer orders).
 */
interface CustomerOrderQueryUseCase {
    fun getById(id: Long): CustomerOrder
    fun list(query: ListCustomerOrdersQuery): Page<CustomerOrder>
}
