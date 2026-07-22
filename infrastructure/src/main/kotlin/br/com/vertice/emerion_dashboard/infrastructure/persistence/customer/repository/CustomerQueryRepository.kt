package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.model.CustomerJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.projection.CustomerProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

/**
 * Read-only side of customer persistence: native SQL mapped straight to
 * `CustomerProjection` (mirrors emerion-load-service's
 * repository/<x>QueryRepository native-query + projection pattern), kept
 * separate from `CustomerSpringDataRepository` (JPA entity, writes/upserts
 * only). Postgres supports `Pageable`-driven native queries directly, so
 * (unlike load-service's Firebird `JdbcTemplate` workaround) a plain
 * Spring Data `@Query(nativeQuery = true)` is enough here.
 */
interface CustomerQueryRepository : Repository<CustomerJpaEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                nome_fantasia AS nomeFantasia,
                razao_social AS razaoSocial,
                cpf_cnpj AS cpfCnpj,
                inscricao_estadual AS inscricaoEstadual,
                regime_tributario AS regimeTributario,
                bloqueado,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM customer
            WHERE id = :id
        """,
    )
    fun findProjectionById(@Param("id") id: Long): CustomerProjection?

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                nome_fantasia AS nomeFantasia,
                razao_social AS razaoSocial,
                cpf_cnpj AS cpfCnpj,
                inscricao_estadual AS inscricaoEstadual,
                regime_tributario AS regimeTributario,
                bloqueado,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM customer
            WHERE (:bloqueado IS NULL OR bloqueado = :bloqueado)
              AND (:nomeFantasiaContains IS NULL OR LOWER(nome_fantasia) LIKE LOWER(CONCAT('%', CAST(:nomeFantasiaContains AS text), '%')))
        """,
        countQuery = """
            SELECT count(*)
            FROM customer
            WHERE (:bloqueado IS NULL OR bloqueado = :bloqueado)
              AND (:nomeFantasiaContains IS NULL OR LOWER(nome_fantasia) LIKE LOWER(CONCAT('%', CAST(:nomeFantasiaContains AS text), '%')))
        """,
    )
    fun search(
        @Param("bloqueado") bloqueado: Boolean?,
        @Param("nomeFantasiaContains") nomeFantasiaContains: String?,
        pageable: Pageable,
    ): Page<CustomerProjection>
}
