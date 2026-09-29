package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.CustomerJpaEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.CustomerProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CustomerRepository : JpaRepository<CustomerJpaEntity, Long> {

    fun findByExternalId(externalId: String): CustomerJpaEntity?

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                cnpj_empresa AS cnpjEmpresa,
                nome_fantasia AS nomeFantasia,
                razao_social AS razaoSocial,
                cpf_cnpj AS cpfCnpj,
                inscricao_estadual AS inscricaoEstadual,
                regime_tributario AS regimeTributario,
                bloqueado,
                data_nascimento AS dataNascimento,
                data_cadastro AS dataCadastro,
                data_ultima_atualizacao AS dataUltimaAtualizacao,
                email1,
                email2,
                website,
                limite_credito AS limiteCredito,
                observacoes,
                cnae,
                vendedor_external_id AS vendedorExternalId,
                nome_vendedor AS nomeVendedor,
                codigo_tipo_cliente AS codigoTipoCliente,
                codigo_grupo_cliente AS codigoGrupoCliente,
                codigo_categoria_cliente AS codigoCategoriaCliente,
                uf,
                macro_regiao AS macroRegiao,
                micro_regiao AS microRegiao,
                setor,
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
                cnpj_empresa AS cnpjEmpresa,
                nome_fantasia AS nomeFantasia,
                razao_social AS razaoSocial,
                cpf_cnpj AS cpfCnpj,
                inscricao_estadual AS inscricaoEstadual,
                regime_tributario AS regimeTributario,
                bloqueado,
                data_nascimento AS dataNascimento,
                data_cadastro AS dataCadastro,
                data_ultima_atualizacao AS dataUltimaAtualizacao,
                email1,
                email2,
                website,
                limite_credito AS limiteCredito,
                observacoes,
                cnae,
                vendedor_external_id AS vendedorExternalId,
                nome_vendedor AS nomeVendedor,
                codigo_tipo_cliente AS codigoTipoCliente,
                codigo_grupo_cliente AS codigoGrupoCliente,
                codigo_categoria_cliente AS codigoCategoriaCliente,
                uf,
                macro_regiao AS macroRegiao,
                micro_regiao AS microRegiao,
                setor,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM customer
            WHERE (:bloqueado IS NULL OR bloqueado = :bloqueado)
              AND (:nomeFantasiaContains IS NULL OR LOWER(nome_fantasia) LIKE LOWER(CONCAT('%', CAST(:nomeFantasiaContains AS text), '%')))
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
        countQuery = """
            SELECT count(*)
            FROM customer
            WHERE (:bloqueado IS NULL OR bloqueado = :bloqueado)
              AND (:nomeFantasiaContains IS NULL OR LOWER(nome_fantasia) LIKE LOWER(CONCAT('%', CAST(:nomeFantasiaContains AS text), '%')))
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
    )
    fun search(
        @Param("bloqueado") bloqueado: Boolean?,
        @Param("nomeFantasiaContains") nomeFantasiaContains: String?,
        @Param("cnpjEmpresa") cnpjEmpresa: String?,
        pageable: Pageable,
    ): Page<CustomerProjection>
}
