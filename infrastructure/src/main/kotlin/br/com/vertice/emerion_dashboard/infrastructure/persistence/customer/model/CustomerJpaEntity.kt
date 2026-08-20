package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.model

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

    @Column(name = "data_nascimento")
    var dataNascimento: LocalDate? = null,

    @Column(name = "data_cadastro")
    var dataCadastro: LocalDate? = null,

    @Column(name = "data_ultima_atualizacao")
    var dataUltimaAtualizacao: LocalDate? = null,

    @Column(name = "email1")
    var email1: String? = null,

    @Column(name = "email2")
    var email2: String? = null,

    @Column(name = "website")
    var website: String? = null,

    @Column(name = "limite_credito")
    var limiteCredito: BigDecimal? = null,

    @Column(name = "observacoes")
    var observacoes: String? = null,

    @Column(name = "cnae")
    var cnae: String? = null,

    @Column(name = "vendedor_external_id")
    var vendedorExternalId: Long? = null,

    @Column(name = "nome_vendedor")
    var nomeVendedor: String? = null,

    @Column(name = "codigo_tipo_cliente")
    var codigoTipoCliente: String? = null,

    @Column(name = "codigo_grupo_cliente")
    var codigoGrupoCliente: String? = null,

    @Column(name = "codigo_categoria_cliente")
    var codigoCategoriaCliente: String? = null,

    @Column(name = "uf")
    var uf: String? = null,

    @Column(name = "macro_regiao")
    var macroRegiao: String? = null,

    @Column(name = "micro_regiao")
    var microRegiao: String? = null,

    @Column(name = "setor")
    var setor: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
