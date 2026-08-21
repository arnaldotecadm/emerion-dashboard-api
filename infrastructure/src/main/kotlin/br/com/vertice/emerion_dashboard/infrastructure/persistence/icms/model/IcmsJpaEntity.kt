package br.com.vertice.emerion_dashboard.infrastructure.persistence.icms.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(
    name = "icms",
    uniqueConstraints = [UniqueConstraint(
        name = "uk_icms_cnpj_empresa_codigo_icms",
        columnNames = ["cnpj_empresa", "codigo_icms"],
    )],
)
class IcmsJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @Column(name = "cnpj_empresa", nullable = false) var cnpjEmpresa: String = "",
    @Column(name = "codigo_icms", nullable = false) var codigoIcms: String = "",
    @Column(name = "tipo_icms") var tipoIcms: String? = null,
    @Column(name = "nome_icms") var nomeIcms: String? = null,
    @Column(name = "uf_emitente") var ufEmitente: String? = null,
    @Column(name = "codigo_regime_tributario") var codigoRegimeTributario: String? = null,
    @Column(name = "aliquota_icms") var aliquotaIcms: BigDecimal? = null,
    @Column(name = "percentual_reducao_valor_imposto") var percentualReducaoValorImposto: BigDecimal? = null,
    @Column(name = "percentual_base_calculo_icms") var percentualBaseCalculoIcms: BigDecimal? = null,
    @Column(name = "situacao_tributaria_icms") var situacaoTributariaIcms: String? = null,
    @Column(name = "created_at", nullable = false) var createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: Instant = Instant.now(),
)
