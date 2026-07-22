package br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model

import java.math.BigDecimal
import java.time.Instant

/** Input command for a single customer order inside an ingestion batch. */
data class IngestCustomerOrderCommand(
    val externalId: String,
    val codCli: String,
    val cnpjEmpresa: String?,
    val nronfe: String?,
    val dteres: Instant,
    val sitres: String?,
    val totger: BigDecimal,
    val totres: BigDecimal,
    val totipi: BigDecimal,
    val totsub: BigDecimal,
    val totdescinc: BigDecimal,
    val itens: List<IngestCustomerOrderItemCommand>,
)
