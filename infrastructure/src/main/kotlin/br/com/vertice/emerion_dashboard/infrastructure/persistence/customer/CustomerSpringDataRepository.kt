package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer

import org.springframework.data.domain.Page as SpringPage
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CustomerSpringDataRepository : JpaRepository<CustomerJpaEntity, Long> {

    fun findByExternalId(externalId: String): CustomerJpaEntity?

    @Query(
        """
        SELECT c FROM CustomerJpaEntity c
        WHERE (:status IS NULL OR c.status = :status)
          AND (:nameContains IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :nameContains, '%')))
        """,
    )
    fun search(
        @Param("status") status: CustomerStatusJpa?,
        @Param("nameContains") nameContains: String?,
        pageable: Pageable,
    ): SpringPage<CustomerJpaEntity>
}
