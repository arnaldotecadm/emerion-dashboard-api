package br.com.vertice.emerion_dashboard.application.fincre.ingestion.model

import java.math.BigDecimal
import java.time.LocalDate

data class IngestFincreParcelaCommand(
    val numeroParcela: Int,
    val flagIncobravel: String? = null,
    val dataIncobravel: LocalDate? = null,
    val dataVencimento: LocalDate? = null,
    val prazoEmDias: Int? = null,
    val valorParcela: BigDecimal? = null,
    val numeroBancario: String? = null,
    val codigoBanco: String? = null,
    val nomeBanco: String? = null,
    val observacoes: String? = null,
    val flagCartaAnuencia: String? = null,
    val dataCartaAnuencia: LocalDate? = null,
    val flagPago: String? = null,
)
