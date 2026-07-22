package br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.Instant

/**
 * JPA entity for the `customer_credit` table. Lives entirely in the
 * infrastructure layer: the domain layer never sees this class, only
 * `domain.customercredit.model.CustomerCredit` via
 * `CustomerCreditPersistenceMapper`.
 */
@Entity
@Table(
    name = "customer_credit",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_customer_credit_customer_sequencia", columnNames = ["customer_external_id", "sequencia"]),
    ],
)
class CustomerCreditJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "customer_external_id", nullable = false)
    var customerExternalId: String = "",

    @Column(name = "sequencia")
    var sequencia: String? = null,

    @Column(name = "data", nullable = false)
    var data: Instant = Instant.now(),

    @Column(name = "data_pedido")
    var dataPedido: Instant? = null,

    @Column(name = "valor_utilizado", nullable = false)
    var valorUtilizado: BigDecimal = BigDecimal.ZERO,

    @Column(name = "valor_total", nullable = false)
    var valorTotal: BigDecimal = BigDecimal.ZERO,

    @Column(name = "saldo", nullable = false)
    var saldo: BigDecimal = BigDecimal.ZERO,

    @Column(name = "situacao")
    var situacao: String? = null,

    @Column(name = "tipo", nullable = false)
    var tipo: String = "",

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
