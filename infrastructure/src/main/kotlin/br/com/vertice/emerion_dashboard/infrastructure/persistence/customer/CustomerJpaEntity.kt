package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

enum class CustomerStatusJpa {
    ACTIVE,
    INACTIVE,
    UNKNOWN,
}

/**
 * JPA entity for the `customer` table. Lives entirely in the infrastructure
 * layer: the domain layer never sees this class, only
 * `domain.customer.Customer` via `CustomerPersistenceMapper`.
 */
@Entity
@Table(
    name = "customer",
    uniqueConstraints = [UniqueConstraint(name = "uk_customer_external_id", columnNames = ["external_id"])],
)
class CustomerJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "external_id", nullable = false)
    var externalId: String = "",

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "email")
    var email: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: CustomerStatusJpa = CustomerStatusJpa.UNKNOWN,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
