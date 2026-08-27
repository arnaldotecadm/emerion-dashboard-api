package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinColumns
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal

@Entity
@Table(
    name = "liberacao_detalhe",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_liberacao_detalhe_sequencia",
            columnNames = ["numero_pedido", "numero_liberacao", "numero_sequencia_liberacao"],
        ),
    ],
)
class LiberacaoDetalheJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        value = [
            JoinColumn(name = "numero_pedido", referencedColumnName = "numero_pedido", nullable = false),
            JoinColumn(name = "numero_liberacao", referencedColumnName = "numero_liberacao", nullable = false),
        ],
    )
    var liberacao: LiberacaoJpaEntity? = null,
    @Column(name = "numero_sequencia_liberacao", nullable = false)
    var numeroSequenciaLiberacao: Int = 0,
    @Column(name = "classificacao_item", nullable = false)
    var classificacaoItem: String = "",
    @Column(name = "codigo_grupo", nullable = false)
    var codigoGrupo: String = "",
    @Column(name = "codigo_sub_grupo", nullable = false)
    var codigoSubGrupo: String = "",
    @Column(name = "codigo_produto", nullable = false)
    var codigoProduto: String = "",
    @Column(name = "descricao_item_liberacao", nullable = false)
    var descricaoItemLiberacao: String = "",
    @Column(name = "quantidade_no_pedido", nullable = false)
    var quantidadeNoPedido: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_separado", nullable = false)
    var totalSeparado: BigDecimal = BigDecimal.ZERO,
    @Column(name = "quantidade_restante", nullable = false)
    var quantidadeRestante: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_valor_liquido", nullable = false)
    var totalValorLiquido: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_valor_bruto", nullable = false)
    var totalValorBruto: BigDecimal = BigDecimal.ZERO,
    @Column(name = "percentual_desconto", nullable = false)
    var percentualDesconto: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_custo", nullable = false)
    var totalCusto: BigDecimal = BigDecimal.ZERO,
    @Column(name = "percentual_de_acrescimo", nullable = false)
    var percentualDeAcrescimo: BigDecimal = BigDecimal.ZERO,
    @Column(name = "preco_venda_item", nullable = false)
    var precoVendaItem: BigDecimal = BigDecimal.ZERO,
    @Column(name = "preco_praticado", nullable = false)
    var precoPraticado: BigDecimal = BigDecimal.ZERO,
    @Column(name = "custo_praticado", nullable = false)
    var custoPraticado: BigDecimal = BigDecimal.ZERO,
)
