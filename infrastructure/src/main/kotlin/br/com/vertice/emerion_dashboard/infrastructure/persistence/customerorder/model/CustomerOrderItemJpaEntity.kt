package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal

/**
 * JPA entity for one row of the `customer_order_item` table (one line item
 * of a customer order). Lives entirely in the infrastructure layer.
 */
@Entity
@Table(
    name = "customer_order_item",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_customer_order_item_seq_re2", columnNames = ["customer_order_id", "seq_re2"]),
    ],
)
class CustomerOrderItemJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_order_id", nullable = false)
    var customerOrder: CustomerOrderJpaEntity? = null,

    @Column(name = "produto", nullable = false)
    var produto: String = "",

    @Column(name = "descricao")
    var descricao: String? = null,

    @Column(name = "quantidade", nullable = false)
    var quantidade: BigDecimal = BigDecimal.ZERO,

    @Column(name = "valor_unitario", nullable = false)
    var valorUnitario: BigDecimal = BigDecimal.ZERO,

    @Column(name = "valor_total", nullable = false)
    var valorTotal: BigDecimal = BigDecimal.ZERO,

    @Column(name = "seq_re2", nullable = false)
    var seqRe2: Int = 0,

    @Column(name = "cod_clp")
    var codClp: String? = null,

    @Column(name = "cod_st1")
    var codSt1: String? = null,

    @Column(name = "cod_und")
    var codUnd: String? = null,

    @Column(name = "vlu_re2")
    var vluRe2: BigDecimal? = null,

    @Column(name = "dsc_re2")
    var dscRe2: BigDecimal? = null,

    @Column(name = "dsr_re2")
    var dsrRe2: BigDecimal? = null,

    @Column(name = "icms_aliquota")
    var icmsAliquota: BigDecimal? = null,

    @Column(name = "icms_base")
    var icmsBase: BigDecimal? = null,

    @Column(name = "icms_valor")
    var icmsValor: BigDecimal? = null,

    @Column(name = "icms_reducao_base")
    var icmsReducaoBase: BigDecimal? = null,

    @Column(name = "icms_substituicao_base")
    var icmsSubstituicaoBase: BigDecimal? = null,

    @Column(name = "icms_substituicao_valor")
    var icmsSubstituicaoValor: BigDecimal? = null,

    @Column(name = "icms_substituicao_aliquota")
    var icmsSubstituicaoAliquota: BigDecimal? = null,

    @Column(name = "icms_substituicao_margem")
    var icmsSubstituicaoMargem: BigDecimal? = null,

    @Column(name = "icms_substituicao_reducao_base")
    var icmsSubstituicaoReducaoBase: BigDecimal? = null,

    @Column(name = "ipi_aliquota")
    var ipiAliquota: BigDecimal? = null,

    @Column(name = "ipi_base")
    var ipiBase: BigDecimal? = null,

    @Column(name = "ipi_valor")
    var ipiValor: BigDecimal? = null,

    @Column(name = "ipi_classificacao")
    var ipiClassificacao: String? = null,

    @Column(name = "ipi_cst")
    var ipiCst: String? = null,

    @Column(name = "pis_base")
    var pisBase: BigDecimal? = null,

    @Column(name = "pis_aliquota")
    var pisAliquota: BigDecimal? = null,

    @Column(name = "pis_valor")
    var pisValor: BigDecimal? = null,

    @Column(name = "pis_cst")
    var pisCst: String? = null,

    @Column(name = "cofins_base")
    var cofinsBase: BigDecimal? = null,

    @Column(name = "cofins_aliquota")
    var cofinsAliquota: BigDecimal? = null,

    @Column(name = "cofins_valor")
    var cofinsValor: BigDecimal? = null,

    @Column(name = "cofins_cst")
    var cofinsCst: String? = null,

    @Column(name = "desconto_valor")
    var descontoValor: BigDecimal? = null,

    @Column(name = "frete_valor")
    var freteValor: BigDecimal? = null,

    @Column(name = "seguro_valor")
    var seguroValor: BigDecimal? = null,

    @Column(name = "outras_despesas_valor")
    var outrasDespesasValor: BigDecimal? = null,

    @Column(name = "total_item_tributado")
    var totalItemTributado: BigDecimal? = null,

    @Column(name = "tot_ren")
    var totRen: BigDecimal? = null,

    @Column(name = "tot_ge2")
    var totGe2: BigDecimal? = null,

    @Column(name = "observacao")
    var observacao: String? = null,

    @Column(name = "pedido_compra_cliente")
    var pedidoCompraCliente: String? = null,

    @Column(name = "item_pedido_compra_cliente")
    var itemPedidoCompraCliente: Int? = null,

    @Column(name = "nro_re2")
    var nroRe2: Int? = null,

    @Column(name = "flg_val")
    var flgVal: String? = null,

    @Column(name = "flg_pac")
    var flgPac: String? = null,

    @Column(name = "flg_lib")
    var flgLib: String? = null,

    @Column(name = "cod_cfo")
    var codCfo: String? = null,
)
