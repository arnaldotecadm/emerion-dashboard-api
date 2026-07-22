package br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model

import java.math.BigDecimal

/** Input command for a single line item inside a customer order ingestion item. */
data class IngestCustomerOrderItemCommand(
    val produto: String,
    val descricao: String?,
    val quantidade: BigDecimal,
    val valorUnitario: BigDecimal,
    val valorTotal: BigDecimal,
)
