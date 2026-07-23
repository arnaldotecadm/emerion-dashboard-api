package br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model

import java.math.BigDecimal
import java.time.LocalDate

/** Input command for a single customer order inside an ingestion batch. */
data class IngestCustomerOrderCommand(
    val externalId: String,
    val codCli: String,
    val cnpjEmpresa: String,
    val cpfCnpj: String?,
    val nronfe: String?,
    val dteres: LocalDate,
    val sitres: String?,
    val totger: BigDecimal,
    val totres: BigDecimal,
    val totipi: BigDecimal,
    val totsub: BigDecimal,
    val totdescinc: BigDecimal,
    val itens: List<IngestCustomerOrderItemCommand>,
)
