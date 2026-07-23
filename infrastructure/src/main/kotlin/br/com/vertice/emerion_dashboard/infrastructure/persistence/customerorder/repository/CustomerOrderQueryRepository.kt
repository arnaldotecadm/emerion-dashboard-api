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
                cpf_cnpj AS cpfCnpj,
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
                cpf_cnpj AS cpfCnpj,
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
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
        countQuery = """
            SELECT count(*)
            FROM customer_order
            WHERE (:codCli IS NULL OR cod_cli = :codCli)
              AND (:sitres IS NULL OR sitres = :sitres)
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
    )
    fun searchHeaders(
        @Param("codCli") codCli: String?,
        @Param("sitres") sitres: String?,
        @Param("cnpjEmpresa") cnpjEmpresa: String?,
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
                valor_total AS valorTotal,
                seq_re2 AS seqRe2,
                cod_clp AS codClp,
                cod_st1 AS codSt1,
                cod_und AS codUnd,
                vlu_re2 AS vluRe2,
                dsc_re2 AS dscRe2,
                dsr_re2 AS dsrRe2,
                icms_aliquota AS icmsAliquota,
                icms_base AS icmsBase,
                icms_valor AS icmsValor,
                icms_reducao_base AS icmsReducaoBase,
                icms_substituicao_base AS icmsSubstituicaoBase,
                icms_substituicao_valor AS icmsSubstituicaoValor,
                icms_substituicao_aliquota AS icmsSubstituicaoAliquota,
                icms_substituicao_margem AS icmsSubstituicaoMargem,
                icms_substituicao_reducao_base AS icmsSubstituicaoReducaoBase,
                ipi_aliquota AS ipiAliquota,
                ipi_base AS ipiBase,
                ipi_valor AS ipiValor,
                ipi_classificacao AS ipiClassificacao,
                ipi_cst AS ipiCst,
                pis_base AS pisBase,
                pis_aliquota AS pisAliquota,
                pis_valor AS pisValor,
                pis_cst AS pisCst,
                cofins_base AS cofinsBase,
                cofins_aliquota AS cofinsAliquota,
                cofins_valor AS cofinsValor,
                cofins_cst AS cofinsCst,
                desconto_valor AS descontoValor,
                frete_valor AS freteValor,
                seguro_valor AS seguroValor,
                outras_despesas_valor AS outrasDespesasValor,
                total_item_tributado AS totalItemTributado,
                tot_ren AS totRen,
                tot_ge2 AS totGe2,
                observacao,
                pedido_compra_cliente AS pedidoCompraCliente,
                item_pedido_compra_cliente AS itemPedidoCompraCliente,
                nro_re2 AS nroRe2,
                flg_val AS flgVal,
                flg_pac AS flgPac,
                flg_lib AS flgLib,
                cod_cfo AS codCfo
            FROM customer_order_item
            WHERE customer_order_id IN (:customerOrderIds)
            ORDER BY customer_order_id, produto
        """,
    )
    fun findItemsByCustomerOrderIds(
        @Param("customerOrderIds") customerOrderIds: List<Long>,
    ): List<CustomerOrderItemProjection>
}
