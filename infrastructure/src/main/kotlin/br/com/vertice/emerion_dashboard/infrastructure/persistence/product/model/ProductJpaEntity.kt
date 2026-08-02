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

    @Column(name = "cnpj_empresa", nullable = false)
    var cnpjEmpresa: String = "",

    @Column(name = "nome", nullable = false)
    var nome: String = "",

    @Column(name = "descricao_reduzida")
    var descricaoReduzida: String? = null,

    @Column(name = "referencia_interna")
    var referenciaInterna: String? = null,

    @Column(name = "ncm")
    var ncm: String? = null,

    @Column(name = "cest")
    var cest: String? = null,

    @Column(name = "origem_produto")
    var origemProduto: String? = null,

    @Column(name = "categoria")
    var categoria: String? = null,

    @Column(name = "tipo")
    var tipo: String? = null,

    @Column(name = "marca")
    var marca: String? = null,

    @Column(name = "unidade")
    var unidade: String? = null,

    @Column(name = "peso_liquido")
    var pesoLiquido: BigDecimal? = null,

    @Column(name = "peso_bruto")
    var pesoBruto: BigDecimal? = null,

    @Column(name = "descontinuado")
    var descontinuado: Boolean? = null,

    @Column(name = "codigo_barras")
    var codigoBarras: String? = null,

    @Column(name = "codigo_barras_proprio")
    var codigoBarrasProprio: String? = null,

    @Column(name = "preco")
    var preco: BigDecimal? = null,

    @Column(name = "preco2")
    var preco2: BigDecimal? = null,

    @Column(name = "preco3")
    var preco3: BigDecimal? = null,

    @Column(name = "preco4")
    var preco4: BigDecimal? = null,

    @Column(name = "preco5")
    var preco5: BigDecimal? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
