package br.com.vertice.emerion_dashboard.infrastructure.persistence.product.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.model.ProductJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.projection.ProductProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

/**
 * Read-only side of product persistence: native SQL mapped straight to
 * `ProductProjection` (mirrors emerion-load-service's
 * repository/<x>QueryRepository native-query + projection pattern), kept
 * separate from `ProductSpringDataRepository` (JPA entity, writes/upserts
 * only).
 */
interface ProductQueryRepository : Repository<ProductJpaEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                cnpj_empresa AS cnpjEmpresa,
                nome,
                preco,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM product
            WHERE id = :id
        """,
    )
    fun findProjectionById(@Param("id") id: Long): ProductProjection?

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                cnpj_empresa AS cnpjEmpresa,
                nome,
                preco,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM product
            WHERE (:nomeContains IS NULL OR LOWER(nome) LIKE LOWER(CONCAT('%', CAST(:nomeContains AS text), '%')))
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
        countQuery = """
            SELECT count(*)
            FROM product
            WHERE (:nomeContains IS NULL OR LOWER(nome) LIKE LOWER(CONCAT('%', CAST(:nomeContains AS text), '%')))
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
    )
    fun search(
        @Param("nomeContains") nomeContains: String?,
        @Param("cnpjEmpresa") cnpjEmpresa: String?,
        pageable: Pageable,
    ): Page<ProductProjection>
}
