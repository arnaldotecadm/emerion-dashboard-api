package br.com.vertice.emerion_dashboard.application.fincre.ingestion.model

import java.math.BigDecimal
import java.time.LocalDate

data class IngestFincreCommand(
    val cnpjEmpresa: String,
    val codigoEmpresa: Long? = null,
    val dataEmissao: LocalDate? = null,
    val documento: String,
    val codigoCondicaoRecebimento: String? = null,
    val nomeCondicaoRecebimento: String? = null,
    val nomeEmpresa: String? = null,
    val codigoComissao: Long? = null,
    val percentualComissao: BigDecimal? = null,
    val codigoCliente: Long? = null,
    val nomeCliente: String? = null,
    val codigoVendedor: Long? = null,
    val nomeVendedor: String? = null,
    val codigoTipoDocumento: String? = null,
    val nomeTipoDocumento: String? = null,
    val parcelas: List<IngestFincreParcelaCommand>,
)
