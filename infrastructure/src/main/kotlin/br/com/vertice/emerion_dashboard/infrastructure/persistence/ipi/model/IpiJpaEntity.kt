package br.com.vertice.emerion_dashboard.infrastructure.persistence.ipi.model

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
    name = "ipi",
    uniqueConstraints = [UniqueConstraint(name = "uk_ipi_cnpj_empresa_codigo_ipi", columnNames = ["cnpj_empresa", "codigo_ipi"])],
)
class IpiJpaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @Column(name = "cnpj_empresa", nullable = false) var cnpjEmpresa: String = "",
    @Column(name = "codigo_ipi", nullable = false) var codigoIpi: String = "",
    @Column(name = "flg_ativo") var flgAtivo: String? = null,
    @Column(name = "tipo_ipi") var tipoIpi: String? = null,
    @Column(name = "nome_ipi") var nomeIpi: String? = null,
    @Column(name = "ncm_ipi") var ncmIpi: String? = null,
    @Column(name = "codigo_enquadramento_legal") var codigoEnquadramentoLegal: String? = null,
    @Column(name = "cst_ipi") var cstIpi: String? = null,
    @Column(name = "descricao_situacao_tributaria_ipi") var descricaoSituacaoTributariaIpi: String? = null,
    @Column(name = "aliquota_ipi") var aliquotaIpi: BigDecimal? = null,
    @Column(name = "percentual_base_calculo_ipi") var percentualBaseCalculoIpi: BigDecimal? = null,
    @Column(name = "flg_sineif20") var flgSineif20: String? = null,
    @Column(name = "codigo_texto_fiscal") var codigoTextoFiscal: String? = null,
    @Column(name = "cst_pis") var cstPis: String? = null,
    @Column(name = "descricao_situacao_tributaria_pis") var descricaoSituacaoTributariaPis: String? = null,
    @Column(name = "aliquota_pis") var aliquotaPis: BigDecimal? = null,
    @Column(name = "inclui_desconto_suframa_pis") var incluiDescontoSuframaPis: String? = null,
    @Column(name = "cst_cofins") var cstCofins: String? = null,
    @Column(name = "descricao_situacao_tributaria_cofins") var descricaoSituacaoTributariaCofins: String? = null,
    @Column(name = "aliquota_cofins") var aliquotaCofins: BigDecimal? = null,
    @Column(name = "inclui_desconto_suframa_cofins") var incluiDescontoSuframaCofins: String? = null,
    @Column(name = "created_at", nullable = false) var createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false) var updatedAt: Instant = Instant.now(),
)
