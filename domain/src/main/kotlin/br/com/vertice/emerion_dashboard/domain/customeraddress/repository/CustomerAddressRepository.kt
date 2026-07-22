package br.com.vertice.emerion_dashboard.domain.customeraddress.repository

import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddress
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest

/**
 * Outbound port (driven port) for CustomerAddress persistence. Implemented
 * by an adapter in infrastructure/persistence/customeraddress (JPA today,
 * but callers in domain/application never know that).
 */
interface CustomerAddressRepository {
    fun findById(id: Long): CustomerAddress?

    fun findByExternalId(externalId: String): CustomerAddress?

    fun findAll(
        pageRequest: PageRequest,
        cpfCnpjContains: String?,
    ): Page<CustomerAddress>

    /** Insert or update (by externalId), replacing the enderecos list wholesale, and return the persisted address set. */
    fun save(address: CustomerAddress): CustomerAddress
}
