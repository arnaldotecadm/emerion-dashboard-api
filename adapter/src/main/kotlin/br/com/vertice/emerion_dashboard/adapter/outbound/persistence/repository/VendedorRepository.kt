package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.VendedorJpaEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.VendedorProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VendedorRepository : JpaRepository<VendedorJpaEntity, Long> {
    fun findByExternalId(externalId: String): VendedorJpaEntity?
    fun findByExternalIdAndCnpjEmpresa(externalId: String, cnpjEmpresa: String): VendedorJpaEntity?

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
                ativo,
                saldo,
                data_cadastro AS dataCadastro,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM vendedor
            WHERE id = :id
              AND cnpj_empresa = :cnpjEmpresa
        """,
    )
    fun findProjectionById(@Param("id") id: Long, @Param("cnpjEmpresa") cnpjEmpresa: String): VendedorProjection?

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
                ativo,
                saldo,
                data_cadastro AS dataCadastro,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM vendedor
            WHERE (:nomeContains IS NULL OR LOWER(nome) LIKE LOWER(CONCAT('%', CAST(:nomeContains AS text), '%')))
              AND (:ativo IS NULL OR upper(ativo) = :ativo)
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
        countQuery = """
            SELECT count(*)
            FROM vendedor
            WHERE (:nomeContains IS NULL OR LOWER(nome) LIKE LOWER(CONCAT('%', CAST(:nomeContains AS text), '%')))
              AND (:ativo IS NULL OR upper(ativo) = :ativo)
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
    )
    fun search(
        @Param("nomeContains") nomeContains: String?,
        @Param("ativo") ativo: String?,
        @Param("cnpjEmpresa") cnpjEmpresa: String?,
        pageable: Pageable,
    ): Page<VendedorProjection>
}
