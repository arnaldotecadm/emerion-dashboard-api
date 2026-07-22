package br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.model.CustomerCreditJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerCreditSpringDataRepository : JpaRepository<CustomerCreditJpaEntity, Long> {
    fun findByCustomerExternalIdAndSequencia(customerExternalId: String, sequencia: String): CustomerCreditJpaEntity?
}
