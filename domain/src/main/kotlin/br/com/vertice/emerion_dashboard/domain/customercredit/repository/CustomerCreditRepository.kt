package br.com.vertice.emerion_dashboard.domain.customercredit.repository

import br.com.vertice.emerion_dashboard.domain.customercredit.model.CustomerCredit
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest

/**
 * Outbound port (driven port) for CustomerCredit persistence. Implemented
 * by an adapter in infrastructure/persistence/customercredit (JPA today,
 * but callers in domain/application never know that).
 */
interface CustomerCreditRepository {
    fun findById(id: Long): CustomerCredit?

    /** Looks up an existing entry by its natural key. Returns null if sequencia is null (no reliable key). */
    fun findByCustomerExternalIdAndSequencia(customerExternalId: String, sequencia: String): CustomerCredit?

    fun findAll(
        pageRequest: PageRequest,
        customerExternalId: String?,
        tipo: String?,
    ): Page<CustomerCredit>

    /** Insert or update (by customerExternalId + sequencia) and return the persisted entry. */
    fun save(credit: CustomerCredit): CustomerCredit
}
