package br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.model.CustomerCreditJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface CustomerCreditSpringDataRepository : JpaRepository<CustomerCreditJpaEntity, Long> {
    fun findByCustomerExternalIdAndDataAndSequencia(
        customerExternalId: String,
        data: LocalDate,
        sequencia: String
    ): CustomerCreditJpaEntity?
}
