package br.com.vertice.emerion_dashboard.application.customercredit.query

import br.com.vertice.emerion_dashboard.application.customercredit.query.model.ListCustomerCreditsQuery
import br.com.vertice.emerion_dashboard.domain.customercredit.model.CustomerCredit
import br.com.vertice.emerion_dashboard.domain.shared.Page

/**
 * Inbound port (driving port) for the customer credit read side, consumed
 * by the REST query adapter. `getById` and `list` are grouped in the same
 * interface because they serve the same functional concern (querying
 * customer credit ledger entries).
 */
interface CustomerCreditQueryUseCase {
    fun getById(id: Long): CustomerCredit
    fun list(query: ListCustomerCreditsQuery): Page<CustomerCredit>
}
