package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(
    name = "customer_quotation",
    schema = "emerion_dashboard",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_customer_quotation_cnpj_external_id",
            columnNames = ["cnpj_empresa", "external_id"],
        ),
    ],
)
class CustomerQuotationJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @Column(name = "external_id", nullable = false) var externalId: String = "",
    @Column(name = "cnpj_empresa", nullable = false) var cnpjEmpresa: String = "",
    @Column(name = "codigo_empresa", nullable = false) var codigoEmpresa: Int = 0,
    @Column(name = "codigo_cliente", nullable = false) var codigoCliente: Int = 0,
    @Column(name = "customer_external_id", nullable = false) var customerExternalId: Long = 0,
    @Column(name = "dteres", nullable = false) var dteres: LocalDate = LocalDate.now(),
    @Column(name = "numero_pedido", nullable = false) var numeroPedido: String = "",
    @Column(name = "data_pedido", nullable = false) var dataPedido: LocalDate = LocalDate.now(),
    @Column(name = "total_pedido_com_impostos", nullable = false) var totalPedidoComImpostos: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_pedido_sem_impostos", nullable = false) var totalPedidoSemImpostos: BigDecimal = BigDecimal.ZERO,
    @Column(name = "cpf_cnpj") var cpfCnpj: String? = null,
    @Column(name = "status_pedido") var statusPedido: String? = null,
    @Column(name = "total_ipi", nullable = false) var totalIpi: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_icms", nullable = false) var totalIcms: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_pis", nullable = false) var totalPis: BigDecimal? = BigDecimal.ZERO,
    @Column(name = "total_cofins", nullable = false) var totalCofins: BigDecimal? = BigDecimal.ZERO,
    @Column(name = "total_substituicao_tributaria", nullable = false) var totalSubstituicaoTributaria: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_desconto_incondicional", nullable = false) var totalDescontoIncondicional: BigDecimal? = BigDecimal.ZERO,
    @Column(name = "total_frete", nullable = false) var totalFrete: BigDecimal? = BigDecimal.ZERO,
    @Column(name = "total_seguro", nullable = false) var totalSeguro: BigDecimal? = BigDecimal.ZERO,
    @Column(name = "total_outras_despesas", nullable = false) var totalOutrasDespesas: BigDecimal? = BigDecimal.ZERO,
    @Column(name = "vendedor_external_id") var vendedorExternalId: Long? = null,
    @Column(name = "atendente_cod") var atendenteCod: String? = null,
    @Column(name = "regime_tributario") var regimeTributario: String? = null,
    @Column(name = "nome_regime_tributario") var nomeRegimeTributario: String? = null,
    @Column(name = "codigo_padrao_faturamento") var codigoPadraoFaturamento: String? = null,
    @Column(name = "numero_cotacao") var numeroCotacao: Int? = null,
    @Column(name = "created_at", nullable = false) var createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: Instant = Instant.now(),
)
