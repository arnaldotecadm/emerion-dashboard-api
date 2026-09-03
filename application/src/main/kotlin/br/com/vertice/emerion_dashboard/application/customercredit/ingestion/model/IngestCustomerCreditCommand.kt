package br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

/**
 * Input command for a single customer credit entry inside an ingestion
 * batch. Unlike other resources, there is no independent externalId — the
 * upsert key is (customerExternalId, sequencia); see IngestCustomerCreditsService.
 */
data class IngestCustomerCreditCommand(
    val customerExternalId: String,
    val cnpjEmpresa: String,
    val sequencia: String?,
    val data: LocalDate,
    val dataPedido: LocalDate?,
    val valorUtilizado: BigDecimal,
    val valorTotal: BigDecimal,
    val saldo: BigDecimal,
    val situacao: String?,
    val tipo: String,
)
