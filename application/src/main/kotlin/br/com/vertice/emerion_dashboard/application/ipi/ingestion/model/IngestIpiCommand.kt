package br.com.vertice.emerion_dashboard.application.ipi.ingestion.model

import java.math.BigDecimal

data class IngestIpiCommand(
    val cnpjEmpresa: String,
    val codigoIpi: String,
    val flgAtivo: String? = null,
    val tipoIpi: String? = null,
    val nomeIpi: String? = null,
    val ncmIpi: String? = null,
    val codigoEnquadramentoLegal: String? = null,
    val cstIpi: String? = null,
    val descricaoSituacaoTributariaIpi: String? = null,
    val aliquotaIpi: BigDecimal? = null,
    val percentualBaseCalculoIpi: BigDecimal? = null,
    val flgSineif20: String? = null,
    val codigoTextoFiscal: String? = null,
    val cstPis: String? = null,
    val descricaoSituacaoTributariaPis: String? = null,
    val aliquotaPis: BigDecimal? = null,
    val incluiDescontoSuframaPis: String? = null,
    val cstCofins: String? = null,
    val descricaoSituacaoTributariaCofins: String? = null,
    val aliquotaCofins: BigDecimal? = null,
    val incluiDescontoSuframaCofins: String? = null,
)
