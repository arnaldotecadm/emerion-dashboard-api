package br.com.vertice.emerion_dashboard.infrastructure.persistence.product.model

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
 * JPA entity for the `product` table. Lives entirely in the infrastructure
 * layer: the domain layer never sees this class, only
 * `domain.product.model.Product` via `ProductPersistenceMapper`.
 */
@Entity
@Table(
    name = "product",
    uniqueConstraints = [UniqueConstraint(name = "uk_product_external_id", columnNames = ["external_id"])],
)
class ProductJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "external_id", nullable = false)
    var externalId: String = "",

    @Column(name = "nome", nullable = false)
    var nome: String = "",

    @Column(name = "preco")
    var preco: BigDecimal? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
