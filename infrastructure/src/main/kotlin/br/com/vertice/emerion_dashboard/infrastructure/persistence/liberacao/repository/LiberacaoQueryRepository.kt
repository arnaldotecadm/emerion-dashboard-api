package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.model.LiberacaoJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoDetalheProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.TopSoldItemProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.TopLiberacaoCustomerProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoSalesByUfProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoSalespersonSummaryProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoSalespersonTotalsProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface LiberacaoQueryRepository : Repository<LiberacaoJpaEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                cnpj_empresa AS cnpjEmpresa,
                codigo_empresa AS codigoEmpresa,
                data_pedido AS dataPedido,
                data_faturamento AS dataFaturamento,
                numero_pedido AS numeroPedido,
                numero_liberacao AS numeroLiberacao,
                data_liberacao AS dataLiberacao,
                hora_liberacao AS horaLiberacao,
                codigo_cliente AS codigoCliente,
                quantidade_separada AS quantidadeSeparada,
                total_liberado_sem_impostos AS totalLiberadoSemImpostos,
                total_liberado_com_impostos AS totalLiberadoComImpostos,
                situacao_liberacao AS situacaoLiberacao,
                codigo_vendedor AS codigoVendedor,
                comissao_liberacao AS comissaoLiberacao,
                total_custo AS totalCusto,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM liberacao
            WHERE id = :id
              AND cnpj_empresa = :cnpjEmpresa
        """,
    )
    fun findProjectionById(
        @Param("id") id: Long,
        @Param("cnpjEmpresa") cnpjEmpresa: String,
    ): LiberacaoProjection?

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                cnpj_empresa AS cnpjEmpresa,
                codigo_empresa AS codigoEmpresa,
                data_pedido AS dataPedido,
                data_faturamento AS dataFaturamento,
                numero_pedido AS numeroPedido,
                numero_liberacao AS numeroLiberacao,
                data_liberacao AS dataLiberacao,
                hora_liberacao AS horaLiberacao,
                codigo_cliente AS codigoCliente,
                quantidade_separada AS quantidadeSeparada,
                total_liberado_sem_impostos AS totalLiberadoSemImpostos,
                total_liberado_com_impostos AS totalLiberadoComImpostos,
                situacao_liberacao AS situacaoLiberacao,
                codigo_vendedor AS codigoVendedor,
                comissao_liberacao AS comissaoLiberacao,
                total_custo AS totalCusto,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM liberacao
            WHERE (:numeroPedido IS NULL OR numero_pedido = :numeroPedido)
              AND cnpj_empresa = :cnpjEmpresa
            ORDER BY data_liberacao DESC, id DESC
        """,
        countQuery = """
            SELECT count(*)
            FROM liberacao
            WHERE (:numeroPedido IS NULL OR numero_pedido = :numeroPedido)
              AND cnpj_empresa = :cnpjEmpresa
        """,
    )
    fun search(
        @Param("numeroPedido") numeroPedido: Long?,
        @Param("cnpjEmpresa") cnpjEmpresa: String,
        pageable: Pageable,
    ): Page<LiberacaoProjection>

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                numero_sequencia_liberacao AS numeroSequenciaLiberacao,
                classificacao_item AS classificacaoItem,
                codigo_grupo AS codigoGrupo,
                codigo_sub_grupo AS codigoSubGrupo,
                codigo_produto AS codigoProduto,
                descricao_item_liberacao AS descricaoItemLiberacao,
                quantidade_no_pedido AS quantidadeNoPedido,
                total_separado AS totalSeparado,
                quantidade_restante AS quantidadeRestante,
                total_valor_liquido AS totalValorLiquido,
                total_valor_bruto AS totalValorBruto,
                percentual_desconto AS percentualDesconto,
                total_custo AS totalCusto,
                percentual_de_acrescimo AS percentualDeAcrescimo,
                preco_venda_item AS precoVendaItem,
                preco_praticado AS precoPraticado,
                custo_praticado AS custoPraticado
            FROM liberacao_detalhe
            WHERE numero_pedido = :numeroPedido
              AND numero_liberacao = :numeroLiberacao
            ORDER BY numero_sequencia_liberacao
        """,
    )
    fun findDetalhes(
        @Param("numeroPedido") numeroPedido: Long,
        @Param("numeroLiberacao") numeroLiberacao: Int,
    ): List<LiberacaoDetalheProjection>

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                ld.codigo_grupo AS codigoGrupo,
                ld.codigo_sub_grupo AS codigoSubGrupo,
                ld.codigo_produto AS codigoProduto,
                MAX(ld.descricao_item_liberacao) AS descricaoItemLiberacao,
                COALESCE(SUM(ld.total_separado), 0) AS totalSeparado
            FROM emerion_dashboard.liberacao_detalhe ld
            LEFT JOIN emerion_dashboard.liberacao l
                on l.numero_liberacao = ld.numero_liberacao
                and l.numero_pedido = ld.numero_pedido
            WHERE l.cnpj_empresa = :cnpjEmpresa
              AND l.data_faturamento >= :startDate
              AND l.data_faturamento <= :endDate
            GROUP BY ld.codigo_grupo, ld.codigo_sub_grupo, ld.codigo_produto
            ORDER BY totalSeparado DESC, ld.codigo_grupo, ld.codigo_sub_grupo, ld.codigo_produto
            LIMIT 15
        """,
    )
    fun findTopSoldItems(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("cnpjEmpresa") cnpjEmpresa: String,
    ): List<TopSoldItemProjection>

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                l.codigo_cliente AS codigoCliente,
                MAX(c.razao_social) AS razaoSocial,
                COALESCE(SUM(l.total_liberado_com_impostos), 0) AS totalLiberadoComImpostos
            FROM emerion_dashboard.liberacao l
            JOIN emerion_dashboard.customer c
                ON c.external_id::int = l.codigo_cliente
               AND c.cnpj_empresa = l.cnpj_empresa
               AND c.bloqueado = false
            WHERE l.cnpj_empresa = :cnpjEmpresa
              AND l.data_faturamento >= :startDate
              AND l.data_faturamento <= :endDate
            GROUP BY l.codigo_cliente
            ORDER BY totalLiberadoComImpostos DESC, l.codigo_cliente
            LIMIT 10
        """,
    )
    fun findTopCustomers(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("cnpjEmpresa") cnpjEmpresa: String,
    ): List<TopLiberacaoCustomerProjection>

    @Query(
        nativeQuery = true,
        value = """
            WITH sales_by_uf AS (
                SELECT
                    COALESCE(c.uf, 'Não informado') AS uf,
                    COALESCE(SUM(l.total_liberado_com_impostos), 0) AS amount
                FROM emerion_dashboard.liberacao l
                JOIN emerion_dashboard.customer c
                    ON c.external_id::int = l.codigo_cliente
                   AND c.cnpj_empresa = l.cnpj_empresa
                   AND c.bloqueado = false
                WHERE l.cnpj_empresa = :cnpjEmpresa
                  AND l.data_faturamento >= :startDate
                  AND l.data_faturamento <= :endDate
                GROUP BY COALESCE(c.uf, 'Não informado')
            )
            SELECT
                uf,
                amount,
                ROUND(
                    amount * 100 / NULLIF(SUM(amount) OVER (), 0),
                    2
                ) AS representationPercentage
            FROM sales_by_uf
            ORDER BY uf
        """,
    )
    fun findSalesByUf(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("cnpjEmpresa") cnpjEmpresa: String,
    ): List<LiberacaoSalesByUfProjection>

    @Query(
        nativeQuery = true,
        value = """
            WITH salesperson_summary AS (
                SELECT
                    v.external_id::bigint AS codigoVendedor,
                    COALESCE(v.nome, '') AS nomeVendedor,
                    COUNT(DISTINCT c.id)::int AS clientesResponsaveis,
                    COUNT(DISTINCT CASE
                        WHEN EXISTS (
                            SELECT 1
                            FROM emerion_dashboard.liberacao l
                            WHERE l.cnpj_empresa = :cnpjEmpresa
                              AND l.codigo_cliente = c.external_id::int
                              AND l.data_faturamento >= :startDate
                              AND l.data_faturamento <= :endDate
                        ) THEN c.id
                    END)::int AS clientesComCompra
                FROM emerion_dashboard.vendedor v
                LEFT JOIN emerion_dashboard.customer c
                    ON c.vendedor_external_id = v.external_id::bigint
                   AND c.cnpj_empresa = :cnpjEmpresa
                   AND c.bloqueado = false
                WHERE v.cnpj_empresa = :cnpjEmpresa
                GROUP BY v.external_id, v.nome
            )
            SELECT
                codigoVendedor,
                nomeVendedor,
                clientesResponsaveis,
                clientesComCompra,
                ROUND(
                    clientesComCompra::numeric * 100 / NULLIF(clientesResponsaveis, 0),
                    2
                ) AS representationPercentage
            FROM salesperson_summary
            WHERE clientesComCompra > 0
            ORDER BY codigoVendedor
        """,
    )
    fun findSalespeople(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("cnpjEmpresa") cnpjEmpresa: String,
    ): List<LiberacaoSalespersonSummaryProjection>

    @Query(
        nativeQuery = true,
        value = """
            WITH period_liberacoes AS (
                SELECT
                    l.cnpj_empresa,
                    l.codigo_vendedor,
                    l.codigo_cliente,
                    l.numero_pedido,
                    l.numero_liberacao
                FROM emerion_dashboard.liberacao l
                WHERE l.cnpj_empresa = :cnpjEmpresa
                  AND l.data_faturamento >= :startDate
                  AND l.data_faturamento <= :endDate
            ),
            liberacao_totals AS (
                SELECT
                    codigo_vendedor,
                    MAX(v.nome) AS nomeVendedor,
                    COALESCE(SUM(total_liberado_com_impostos), 0) AS totalFaturado
                FROM period_liberacoes
                JOIN emerion_dashboard.liberacao l
                    USING (cnpj_empresa, codigo_vendedor, codigo_cliente, numero_pedido, numero_liberacao)
                LEFT JOIN emerion_dashboard.vendedor v
                    ON v.external_id::bigint = codigo_vendedor
                   AND v.cnpj_empresa = :cnpjEmpresa
                GROUP BY codigo_vendedor
            ),
            detail_totals AS (
                SELECT
                    pl.codigo_vendedor,
                    COALESCE(SUM(ld.total_custo), 0) AS custosTotais
                FROM period_liberacoes pl
                JOIN emerion_dashboard.liberacao_detalhe ld
                    ON ld.numero_pedido = pl.numero_pedido
                   AND ld.numero_liberacao = pl.numero_liberacao
                GROUP BY pl.codigo_vendedor
            ),
            icms_totals AS (
                SELECT
                    pl.codigo_vendedor,
                    COALESCE(SUM(co.total_icms), 0) AS totalIcms
                FROM (
                    SELECT DISTINCT cnpj_empresa, codigo_vendedor, codigo_cliente
                    FROM period_liberacoes
                ) pl
                JOIN emerion_dashboard.customer_order co
                    ON co.cnpj_empresa = pl.cnpj_empresa
                   AND co.vendedor_external_id = pl.codigo_vendedor
                   AND co.codigo_cliente = pl.codigo_cliente
                   AND co.data_pedido >= :startDate
                   AND co.data_pedido <= :endDate
                GROUP BY pl.codigo_vendedor
            )
            SELECT
                lt.codigo_vendedor AS codigoVendedor,
                COALESCE(lt.nomeVendedor, '') AS nomeVendedor,
                lt.totalFaturado,
                CAST(0 AS numeric) AS totalComissao,
                CAST(0 AS numeric) AS totalDespesas,
                COALESCE(dt.custosTotais, 0) AS custosTotais,
                CAST(0 AS numeric) AS totalIcms,
                CAST(0 AS numeric) AS totalSemNota
            FROM liberacao_totals lt
            LEFT JOIN detail_totals dt ON dt.codigo_vendedor = lt.codigo_vendedor
            LEFT JOIN icms_totals it ON it.codigo_vendedor = lt.codigo_vendedor
            ORDER BY lt.codigo_vendedor
        """,
    )
    fun findSalespersonTotals(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        @Param("cnpjEmpresa") cnpjEmpresa: String,
    ): List<LiberacaoSalespersonTotalsProjection>
}
