package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.model.CustomerJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Write-only side of customer persistence (upserts via ingestion). Reads go
 * through `CustomerQueryRepository`'s native-query + projection path instead
 * — this interface exists purely so `save`/`findById`/`findByExternalId`
 * (used to locate the existing row to upsert onto) have a plain JPA entity
 * to work with.
 */
interface CustomerSpringDataRepository : JpaRepository<CustomerJpaEntity, Long> {

    fun findByExternalId(externalId: String): CustomerJpaEntity?
}
