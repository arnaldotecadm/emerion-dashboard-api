package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

/**
 * JPA entity for the `customer_order` table (header). Lives entirely in the
 * infrastructure layer: the domain layer never sees this class, only
 * `domain.customerorder.model.CustomerOrder` via
 * `CustomerOrderPersistenceMapper`.
 */
@Entity
@Table(
    name = "customer_order",
    uniqueConstraints = [UniqueConstraint(name = "uk_customer_order_external_id", columnNames = ["external_id"])],
)
class CustomerOrderJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "external_id", nullable = false)
    var externalId: String = "",

    @Column(name = "codigo_empresa", nullable = false)
    var codigoEmpresa: Int = 0,

    @Column(name = "codigo_cliente", nullable = false)
    var codigoCliente: Int = 0,

    @Column(name = "cpf_cnpj")
    var cpfCnpj: String? = null,

    @Column(name = "numero_pedido", nullable = false)
    var numeroPedido: String = "",

    @Column(name = "data_pedido", nullable = false)
    var dataPedido: LocalDate = LocalDate.now(),

    @Column(name = "status_pedido")
    var statusPedido: String? = null,

    @Column(name = "total_pedido_com_impostos", nullable = false)
    var totalPedidoComImpostos: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_pedido_sem_impostos", nullable = false)
    var totalPedidoSemImpostos: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_ipi", nullable = false)
    var totalIpi: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_icms", nullable = false)
    var totalIcms: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_pis", nullable = false)
    var totalPis: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_cofins", nullable = false)
    var totalCofins: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_substituicao_tributaria", nullable = false)
    var totalSubstituicaoTributaria: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_desconto_incondicional", nullable = false)
    var totalDescontoIncondicional: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_frete")
    var totalFrete: BigDecimal? = null,

    @Column(name = "total_seguro")
    var totalSeguro: BigDecimal? = null,

    @Column(name = "total_outras_despesas")
    var totalOutrasDespesas: BigDecimal? = null,

    @Column(name = "vendedor_external_id")
    var vendedorExternalId: Long? = null,

    @Column(name = "data_entrega_prevista")
    var dataEntregaPrevista: LocalDate? = null,

    @Column(name = "codigo_transportadora")
    var codigoTransportadora: String? = null,

    @Column(name = "pedido_anterior")
    var pedidoAnterior: String? = null,

    @Column(name = "regime_tributario")
    var regimeTributario: String? = null,

    @Column(name = "nome_regime_tributario")
    var nomeRegimeTributario: String? = null,

    @Column(name = "codigo_padrao_faturamento")
    var codigoPadraoFaturamento: String? = null,

    @OneToMany(mappedBy = "customerOrder", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var itens: MutableList<CustomerOrderItemJpaEntity> = mutableListOf(),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
