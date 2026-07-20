package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.model.CustomerJpaEntity
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
        WHERE (:bloqueado IS NULL OR c.bloqueado = :bloqueado)
          AND (:nomeFantasiaContains IS NULL OR LOWER(c.nomeFantasia) LIKE LOWER(CONCAT('%', :nomeFantasiaContains, '%')))
        """,
    )
    fun search(
        @Param("bloqueado") bloqueado: Boolean?,
        @Param("nomeFantasiaContains") nomeFantasiaContains: String?,
        pageable: Pageable,
    ): SpringPage<CustomerJpaEntity>
}
