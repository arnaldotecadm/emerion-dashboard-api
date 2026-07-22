package br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.model.CustomerCreditJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.projection.CustomerCreditProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

/**
 * Read-only side of customer credit persistence: native SQL mapped straight
 * to `CustomerCreditProjection`, kept separate from
 * `CustomerCreditSpringDataRepository` (JPA entity, writes/upserts only).
 */
interface CustomerCreditQueryRepository : Repository<CustomerCreditJpaEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                customer_external_id AS customerExternalId,
                sequencia,
                data,
                data_pedido AS dataPedido,
                valor_utilizado AS valorUtilizado,
                valor_total AS valorTotal,
                saldo,
                situacao,
                tipo,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM customer_credit
            WHERE id = :id
        """,
    )
    fun findProjectionById(@Param("id") id: Long): CustomerCreditProjection?

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                customer_external_id AS customerExternalId,
                sequencia,
                data,
                data_pedido AS dataPedido,
                valor_utilizado AS valorUtilizado,
                valor_total AS valorTotal,
                saldo,
                situacao,
                tipo,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM customer_credit
            WHERE (:customerExternalId IS NULL OR customer_external_id = :customerExternalId)
              AND (:tipo IS NULL OR tipo = :tipo)
        """,
        countQuery = """
            SELECT count(*)
            FROM customer_credit
            WHERE (:customerExternalId IS NULL OR customer_external_id = :customerExternalId)
              AND (:tipo IS NULL OR tipo = :tipo)
        """,
    )
    fun search(
        @Param("customerExternalId") customerExternalId: String?,
        @Param("tipo") tipo: String?,
        pageable: Pageable,
    ): Page<CustomerCreditProjection>
}
