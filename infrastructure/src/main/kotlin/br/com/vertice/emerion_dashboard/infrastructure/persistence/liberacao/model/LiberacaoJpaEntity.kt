package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.model

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
import java.time.LocalTime

@Entity
@Table(
    name = "liberacao",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_liberacao_pedido_numero",
            columnNames = ["numero_pedido", "numero_liberacao"],
        ),
    ],
)
class LiberacaoJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,
    @Column(name = "cnpj_empresa", nullable = false)
    var cnpjEmpresa: String = "",
    @Column(name = "codigo_empresa", nullable = false)
    var codigoEmpresa: Long = 0,
    @Column(name = "data_pedido", nullable = false)
    var dataPedido: LocalDate = LocalDate.now(),
    @Column(name = "numero_pedido", nullable = false)
    var numeroPedido: String = "",
    @Column(name = "numero_liberacao", nullable = false)
    var numeroLiberacao: Int = 0,
    @Column(name = "data_liberacao", nullable = false)
    var dataLiberacao: LocalDate = LocalDate.now(),
    @Column(name = "hora_liberacao", nullable = false)
    var horaLiberacao: LocalTime = LocalTime.MIDNIGHT,
    @Column(name = "codigo_cliente", nullable = false)
    var codigoCliente: Long = 0,
    @Column(name = "quantidade_separada", nullable = false)
    var quantidadeSeparada: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_liberado_sem_impostos", nullable = false)
    var totalLiberadoSemImpostos: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_liberado_com_impostos", nullable = false)
    var totalLiberadoComImpostos: BigDecimal = BigDecimal.ZERO,
    @Column(name = "situacao_liberacao", nullable = false)
    var situacaoLiberacao: String = "",
    @Column(name = "codigo_vendedor", nullable = false)
    var codigoVendedor: Long = 0,
    @Column(name = "comissao_liberacao", nullable = false)
    var comissaoLiberacao: BigDecimal = BigDecimal.ZERO,
    @Column(name = "total_custo", nullable = false)
    var totalCusto: BigDecimal = BigDecimal.ZERO,
    @OneToMany(mappedBy = "liberacao", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var detalhes: MutableList<LiberacaoDetalheJpaEntity> = mutableListOf(),
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
