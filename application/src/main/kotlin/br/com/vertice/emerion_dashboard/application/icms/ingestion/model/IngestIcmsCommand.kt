package br.com.vertice.emerion_dashboard.application.icms.ingestion.model

import java.math.BigDecimal

data class IngestIcmsCommand(
    val cnpjEmpresa: String,
    val codigoIcms: String,
    val tipoIcms: String? = null,
    val nomeIcms: String? = null,
    val ufEmitente: String? = null,
    val codigoRegimeTributario: String? = null,
    val aliquotaIcms: BigDecimal? = null,
    val percentualReducaoValorImposto: BigDecimal? = null,
    val percentualBaseCalculoIcms: BigDecimal? = null,
    val situacaoTributariaIcms: String? = null,
)
