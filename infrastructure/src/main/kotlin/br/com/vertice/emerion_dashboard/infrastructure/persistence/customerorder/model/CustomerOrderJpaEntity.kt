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

    @Column(name = "cod_cli", nullable = false)
    var codCli: String = "",

    @Column(name = "cnpj_empresa", nullable = false)
    var cnpjEmpresa: String = "",

    @Column(name = "nronfe")
    var nronfe: String? = null,

    @Column(name = "dteres", nullable = false)
    var dteres: Instant = Instant.now(),

    @Column(name = "sitres")
    var sitres: String? = null,

    @Column(name = "totger", nullable = false)
    var totger: BigDecimal = BigDecimal.ZERO,

    @Column(name = "totres", nullable = false)
    var totres: BigDecimal = BigDecimal.ZERO,

    @Column(name = "totipi", nullable = false)
    var totipi: BigDecimal = BigDecimal.ZERO,

    @Column(name = "totsub", nullable = false)
    var totsub: BigDecimal = BigDecimal.ZERO,

    @Column(name = "totdescinc", nullable = false)
    var totdescinc: BigDecimal = BigDecimal.ZERO,

    @OneToMany(mappedBy = "customerOrder", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var itens: MutableList<CustomerOrderItemJpaEntity> = mutableListOf(),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
