package br.com.vertice.emerion_dashboard.domain.icms.model

import java.math.BigDecimal
import java.time.Instant

data class Icms(
    val id: Long?,
    val cnpjEmpresa: String,
    val codigoIcms: String,
    val tipoIcms: String?,
    val nomeIcms: String?,
    val ufEmitente: String?,
    val codigoRegimeTributario: String?,
    val aliquotaIcms: BigDecimal?,
    val percentualReducaoValorImposto: BigDecimal?,
    val percentualBaseCalculoIcms: BigDecimal?,
    val situacaoTributariaIcms: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    fun mergeFromIngestion(
        tipoIcms: String?,
        nomeIcms: String?,
        ufEmitente: String?,
        codigoRegimeTributario: String?,
        aliquotaIcms: BigDecimal?,
        percentualReducaoValorImposto: BigDecimal?,
        percentualBaseCalculoIcms: BigDecimal?,
        situacaoTributariaIcms: String?,
        now: Instant,
    ) = copy(
        tipoIcms = tipoIcms,
        nomeIcms = nomeIcms,
        ufEmitente = ufEmitente,
        codigoRegimeTributario = codigoRegimeTributario,
        aliquotaIcms = aliquotaIcms,
        percentualReducaoValorImposto = percentualReducaoValorImposto,
        percentualBaseCalculoIcms = percentualBaseCalculoIcms,
        situacaoTributariaIcms = situacaoTributariaIcms,
        updatedAt = now,
    )
}
