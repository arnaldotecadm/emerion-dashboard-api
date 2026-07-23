package br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.model

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
import java.time.Instant

/**
 * JPA entity for the `customer_address` table (header). Lives entirely in
 * the infrastructure layer: the domain layer never sees this class, only
 * `domain.customeraddress.model.CustomerAddress` via
 * `CustomerAddressPersistenceMapper`.
 */
@Entity
@Table(
    name = "customer_address",
    uniqueConstraints = [UniqueConstraint(name = "uk_customer_address_external_id", columnNames = ["external_id"])],
)
class CustomerAddressJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "external_id", nullable = false)
    var externalId: String = "",

    @Column(name = "cnpj_empresa", nullable = false)
    var cnpjEmpresa: String = "",

    @Column(name = "cpf_cnpj")
    var cpfCnpj: String? = null,

    @OneToMany(mappedBy = "customerAddress", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var enderecos: MutableList<CustomerAddressDetailJpaEntity> = mutableListOf(),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
