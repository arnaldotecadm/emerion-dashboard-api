package br.com.vertice.emerion_dashboard.application.customeraddress.query

import br.com.vertice.emerion_dashboard.application.customeraddress.query.model.ListCustomerAddressesQuery
import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddress
import br.com.vertice.emerion_dashboard.domain.shared.Page

/**
 * Inbound port (driving port) for the customer address read side, consumed
 * by the REST query adapter. `getById` and `list` are grouped in the same
 * interface because they serve the same functional concern (querying
 * customer address sets).
 */
interface CustomerAddressQueryUseCase {
    fun getById(id: Long): CustomerAddress
    fun list(query: ListCustomerAddressesQuery): Page<CustomerAddress>
}
