package br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.model

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

/**
 * JPA entity for one row of the `customer_address_detail` table (one
 * address kind, e.g. FATURAMENTO/COBRANCA/COMPRAS/ENTREGA, for a customer).
 * Lives entirely in the infrastructure layer.
 */
@Entity
@Table(
    name = "customer_address_detail",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_customer_address_detail_tipo", columnNames = ["customer_address_id", "tipo"]),
    ],
)
class CustomerAddressDetailJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_address_id", nullable = false)
    var customerAddress: CustomerAddressJpaEntity? = null,

    @Column(name = "tipo", nullable = false)
    var tipo: String = "",

    @Column(name = "cep")
    var cep: String? = null,

    @Column(name = "endereco")
    var endereco: String? = null,

    @Column(name = "numero")
    var numero: String? = null,

    @Column(name = "referencia")
    var referencia: String? = null,

    @Column(name = "bairro")
    var bairro: String? = null,

    @Column(name = "cidade")
    var cidade: String? = null,

    @Column(name = "uf")
    var uf: String? = null,

    @Column(name = "telefone")
    var telefone: String? = null,

    @Column(name = "telefone_contato")
    var telefoneContato: String? = null,

    @Column(name = "complemento")
    var complemento: String? = null,

    @Column(name = "fax")
    var fax: String? = null,
)
