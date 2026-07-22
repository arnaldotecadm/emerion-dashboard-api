package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.model.CustomerOrderJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerOrderSpringDataRepository : JpaRepository<CustomerOrderJpaEntity, Long> {
    fun findByExternalId(externalId: String): CustomerOrderJpaEntity?
}
