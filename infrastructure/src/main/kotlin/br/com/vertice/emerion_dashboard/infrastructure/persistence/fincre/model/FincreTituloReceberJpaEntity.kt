package br.com.vertice.emerion_dashboard.infrastructure.persistence.fincre.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(
    name = "titulo_receber",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_titulo_receber_documento", columnNames = ["documento"]),
        UniqueConstraint(name = "uk_titulo_receber_cnpj_documento", columnNames = ["cnpj_empresa", "documento"]),
    ],
)
class FincreTituloReceberJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "cnpj_empresa", nullable = false)
    var cnpjEmpresa: String = "",

    @Column(name = "codigo_empresa")
    var codigoEmpresa: Long? = null,

    @Column(name = "data_emissao")
    var dataEmissao: LocalDate? = null,

    @Column(name = "documento", nullable = false)
    var documento: String = "",

    @Column(name = "codigo_condicao_recebimento")
    var codigoCondicaoRecebimento: String? = null,

    @Column(name = "nome_condicao_recebimento")
    var nomeCondicaoRecebimento: String? = null,

    @Column(name = "nome_empresa")
    var nomeEmpresa: String? = null,

    @Column(name = "codigo_comissao")
    var codigoComissao: Long? = null,

    @Column(name = "percentual_comissao")
    var percentualComissao: BigDecimal? = null,

    @Column(name = "codigo_cliente")
    var codigoCliente: Long? = null,

    @Column(name = "nome_cliente")
    var nomeCliente: String? = null,

    @Column(name = "codigo_vendedor")
    var codigoVendedor: Long? = null,

    @Column(name = "nome_vendedor")
    var nomeVendedor: String? = null,

    @Column(name = "codigo_tipo_documento")
    var codigoTipoDocumento: String? = null,

    @Column(name = "nome_tipo_documento")
    var nomeTipoDocumento: String? = null,

    @OneToMany(mappedBy = "tituloReceber", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var parcelas: MutableList<FincreParcelaJpaEntity> = mutableListOf(),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
