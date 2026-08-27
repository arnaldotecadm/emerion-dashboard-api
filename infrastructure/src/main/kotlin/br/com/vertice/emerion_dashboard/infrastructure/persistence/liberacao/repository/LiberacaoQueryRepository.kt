package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.model.LiberacaoJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoDetalheProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

interface LiberacaoQueryRepository : Repository<LiberacaoJpaEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                cnpj_empresa AS cnpjEmpresa,
                codigo_empresa AS codigoEmpresa,
                data_pedido AS dataPedido,
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
        """,
    )
    fun findProjectionById(@Param("id") id: Long): LiberacaoProjection?

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                cnpj_empresa AS cnpjEmpresa,
                codigo_empresa AS codigoEmpresa,
                data_pedido AS dataPedido,
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
            ORDER BY data_liberacao DESC, id DESC
        """,
        countQuery = """
            SELECT count(*)
            FROM liberacao
            WHERE (:numeroPedido IS NULL OR numero_pedido = :numeroPedido)
        """,
    )
    fun search(@Param("numeroPedido") numeroPedido: String?, pageable: Pageable): Page<LiberacaoProjection>

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
        @Param("numeroPedido") numeroPedido: String,
        @Param("numeroLiberacao") numeroLiberacao: Int,
    ): List<LiberacaoDetalheProjection>
}
