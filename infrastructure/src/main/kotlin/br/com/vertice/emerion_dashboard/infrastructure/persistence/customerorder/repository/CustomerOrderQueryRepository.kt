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
                codigo_empresa AS codigoEmpresa,
                codigo_cliente AS codigoCliente,
                cpf_cnpj AS cpfCnpj,
                numero_pedido AS numeroPedido,
                data_pedido AS dataPedido,
                status_pedido AS statusPedido,
                total_pedido_com_impostos AS totalPedidoComImpostos,
                total_pedido_sem_impostos AS totalPedidoSemImpostos,
                total_ipi AS totalIpi,
                total_icms AS totalIcms,
                total_pis AS totalPis,
                total_cofins AS totalCofins,
                total_substituicao_tributaria AS totalSubstituicaoTributaria,
                total_desconto_incondicional AS totalDescontoIncondicional,
                total_frete AS totalFrete,
                total_seguro AS totalSeguro,
                total_outras_despesas AS totalOutrasDespesas,
                vendedor_external_id AS vendedorExternalId,
                data_entrega_prevista AS dataEntregaPrevista,
                codigo_transportadora AS codigoTransportadora,
                pedido_anterior AS pedidoAnterior,
                regime_tributario AS regimeTributario,
                nome_regime_tributario AS nomeRegimeTributario,
                codigo_padrao_faturamento AS codigoPadraoFaturamento,
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
                codigo_empresa AS codigoEmpresa,
                codigo_cliente AS codigoCliente,
                cpf_cnpj AS cpfCnpj,
                numero_pedido AS numeroPedido,
                data_pedido AS dataPedido,
                status_pedido AS statusPedido,
                total_pedido_com_impostos AS totalPedidoComImpostos,
                total_pedido_sem_impostos AS totalPedidoSemImpostos,
                total_ipi AS totalIpi,
                total_icms AS totalIcms,
                total_pis AS totalPis,
                total_cofins AS totalCofins,
                total_substituicao_tributaria AS totalSubstituicaoTributaria,
                total_desconto_incondicional AS totalDescontoIncondicional,
                total_frete AS totalFrete,
                total_seguro AS totalSeguro,
                total_outras_despesas AS totalOutrasDespesas,
                vendedor_external_id AS vendedorExternalId,
                data_entrega_prevista AS dataEntregaPrevista,
                codigo_transportadora AS codigoTransportadora,
                pedido_anterior AS pedidoAnterior,
                regime_tributario AS regimeTributario,
                nome_regime_tributario AS nomeRegimeTributario,
                codigo_padrao_faturamento AS codigoPadraoFaturamento,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM customer_order
            WHERE (:codigoCliente IS NULL OR codigo_cliente = :codigoCliente)
              AND (:statusPedido IS NULL OR status_pedido = :statusPedido)
              AND (:codigoEmpresa IS NULL OR codigo_empresa = :codigoEmpresa)
        """,
        countQuery = """
            SELECT count(*)
            FROM customer_order
            WHERE (:codigoCliente IS NULL OR codigo_cliente = :codigoCliente)
              AND (:statusPedido IS NULL OR status_pedido = :statusPedido)
              AND (:codigoEmpresa IS NULL OR codigo_empresa = :codigoEmpresa)
        """,
    )
    fun searchHeaders(
        @Param("codigoCliente") codigoCliente: Int?,
        @Param("statusPedido") statusPedido: String?,
        @Param("codigoEmpresa") codigoEmpresa: Int?,
        pageable: Pageable,
    ): Page<CustomerOrderHeaderProjection>

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                customer_order_id AS customerOrderId,
                cod_emp AS codEmp,
                dteres,
                numres,
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
                cod_cfo AS codCfo,
                codcor,
                codtam,
                descricao_nfe AS descricaoNFe,
                peso_liquido AS pesoLiquido,
                peso_bruto AS pesoBruto,
                referencia,
                quantidade_faturada AS quantidadeFaturada,
                quantidade_separada AS quantidadeSeparada,
                custo_total AS custoTotal,
                lucro_valor AS lucroValor,
                lucro_porcentagem AS lucroPorcentagem
            FROM customer_order_item
            WHERE customer_order_id IN (:customerOrderIds)
            ORDER BY customer_order_id, produto
        """,
    )
    fun findItemsByCustomerOrderIds(
        @Param("customerOrderIds") customerOrderIds: List<Long>,
    ): List<CustomerOrderItemProjection>
}
