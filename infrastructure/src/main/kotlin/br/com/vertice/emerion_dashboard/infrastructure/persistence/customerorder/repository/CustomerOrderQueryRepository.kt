package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.model.CustomerOrderJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.projection.CustomerOrderHeaderProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.projection.CustomerOrderItemProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

/**
 * Read-only side of customer order persistence: native SQL mapped straight
 * to projections (mirrors emerion-load-service's
 * repository/<x>QueryRepository native-query + projection pattern), kept
 * separate from `CustomerOrderSpringDataRepository` (JPA entity,
 * writes/upserts only). Headers are paginated directly (Postgres supports
 * `Pageable`-driven native queries); the matching line items for a page of
 * headers are then fetched in one follow-up query and grouped back onto
 * each header in `CustomerOrderRepositoryAdapter`, exactly like
 * emerion-load-service's `CustomerOrderQueryRepository` does.
 */
interface CustomerOrderQueryRepository : Repository<CustomerOrderJpaEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                cod_cli AS codCli,
                cnpj_empresa AS cnpjEmpresa,
                nronfe,
                dteres,
                sitres,
                totger,
                totres,
                totipi,
                totsub,
                totdescinc,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM customer_order
            WHERE id = :id
        """,
    )
    fun findHeaderProjectionById(@Param("id") id: Long): CustomerOrderHeaderProjection?

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                cod_cli AS codCli,
                cnpj_empresa AS cnpjEmpresa,
                nronfe,
                dteres,
                sitres,
                totger,
                totres,
                totipi,
                totsub,
                totdescinc,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM customer_order
            WHERE (:codCli IS NULL OR cod_cli = :codCli)
              AND (:sitres IS NULL OR sitres = :sitres)
        """,
        countQuery = """
            SELECT count(*)
            FROM customer_order
            WHERE (:codCli IS NULL OR cod_cli = :codCli)
              AND (:sitres IS NULL OR sitres = :sitres)
        """,
    )
    fun searchHeaders(
        @Param("codCli") codCli: String?,
        @Param("sitres") sitres: String?,
        pageable: Pageable,
    ): Page<CustomerOrderHeaderProjection>

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                customer_order_id AS customerOrderId,
                produto,
                descricao,
                quantidade,
                valor_unitario AS valorUnitario,
                valor_total AS valorTotal
            FROM customer_order_item
            WHERE customer_order_id IN (:customerOrderIds)
            ORDER BY customer_order_id, produto
        """,
    )
    fun findItemsByCustomerOrderIds(
        @Param("customerOrderIds") customerOrderIds: List<Long>,
    ): List<CustomerOrderItemProjection>
}
