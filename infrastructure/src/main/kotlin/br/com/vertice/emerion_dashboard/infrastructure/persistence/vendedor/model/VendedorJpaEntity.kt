package br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.model

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

/**
 * JPA entity for the `vendedor` table. Lives entirely in the infrastructure
 * layer: the domain layer never sees this class, only
 * `domain.vendedor.model.Vendedor` via `VendedorPersistenceMapper`.
 */
@Entity
@Table(
    name = "vendedor",
    uniqueConstraints = [UniqueConstraint(name = "uk_vendedor_external_id", columnNames = ["external_id"])],
)
class VendedorJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "external_id", nullable = false)
    var externalId: String = "",

    @Column(name = "nome", nullable = false)
    var nome: String = "",

    @Column(name = "apelido")
    var apelido: String? = null,

    @Column(name = "cpf_cnpj")
    var cpfCnpj: String? = null,

    @Column(name = "telefone")
    var telefone: String? = null,

    @Column(name = "celular")
    var celular: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "cidade")
    var cidade: String? = null,

    @Column(name = "uf")
    var uf: String? = null,

    @Column(name = "situacao")
    var situacao: String? = null,

    @Column(name = "saldo")
    var saldo: BigDecimal? = null,

    @Column(name = "data_cadastro")
    var dataCadastro: LocalDate? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
