package br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.model.CustomerAddressJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerAddressSpringDataRepository : JpaRepository<CustomerAddressJpaEntity, Long> {
    fun findByExternalId(externalId: String): CustomerAddressJpaEntity?
}
