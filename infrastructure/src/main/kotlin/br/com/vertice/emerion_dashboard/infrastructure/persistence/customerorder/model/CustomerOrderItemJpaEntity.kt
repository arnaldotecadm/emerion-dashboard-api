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
        UniqueConstraint(name = "uk_customer_order_item_produto", columnNames = ["customer_order_id", "produto"]),
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
)
