package br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.model.VendedorJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.projection.VendedorProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

/**
 * Read-only side of vendedor persistence: native SQL mapped straight to
 * `VendedorProjection` (mirrors emerion-load-service's
 * repository/<x>QueryRepository native-query + projection pattern), kept
 * separate from `VendedorSpringDataRepository` (JPA entity, writes/upserts
 * only).
 */
interface VendedorQueryRepository : Repository<VendedorJpaEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                cnpj_empresa AS cnpjEmpresa,
                nome,
                apelido,
                cpf_cnpj AS cpfCnpj,
                telefone,
                celular,
                email,
                cidade,
                uf,
                situacao,
                saldo,
                data_cadastro AS dataCadastro,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM vendedor
            WHERE id = :id
        """,
    )
    fun findProjectionById(@Param("id") id: Long): VendedorProjection?

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                cnpj_empresa AS cnpjEmpresa,
                nome,
                apelido,
                cpf_cnpj AS cpfCnpj,
                telefone,
                celular,
                email,
                cidade,
                uf,
                situacao,
                saldo,
                data_cadastro AS dataCadastro,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM vendedor
            WHERE (:nomeContains IS NULL OR LOWER(nome) LIKE LOWER(CONCAT('%', CAST(:nomeContains AS text), '%')))
              AND (:situacao IS NULL OR situacao = :situacao)
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
        countQuery = """
            SELECT count(*)
            FROM vendedor
            WHERE (:nomeContains IS NULL OR LOWER(nome) LIKE LOWER(CONCAT('%', CAST(:nomeContains AS text), '%')))
              AND (:situacao IS NULL OR situacao = :situacao)
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
    )
    fun search(
        @Param("nomeContains") nomeContains: String?,
        @Param("situacao") situacao: String?,
        @Param("cnpjEmpresa") cnpjEmpresa: String?,
        pageable: Pageable,
    ): Page<VendedorProjection>
}
