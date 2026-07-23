package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

/**
 * JPA entity for the `customer` table. Lives entirely in the infrastructure
 * layer: the domain layer never sees this class, only
 * `domain.customer.model.Customer` via `CustomerPersistenceMapper`.
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

    @Column(name = "cnpj_empresa", nullable = false)
    var cnpjEmpresa: String = "",

    @Column(name = "nome_fantasia", nullable = false)
    var nomeFantasia: String = "",

    @Column(name = "razao_social", nullable = false)
    var razaoSocial: String = "",

    @Column(name = "cpf_cnpj", nullable = false)
    var cpfCnpj: String = "",

    @Column(name = "inscricao_estadual")
    var inscricaoEstadual: String? = null,

    @Column(name = "regime_tributario")
    var regimeTributario: String? = null,

    @Column(name = "bloqueado", nullable = false)
    var bloqueado: Boolean = false,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
