package br.com.vertice.emerion_dashboard.application.product.ingestion.model

import java.math.BigDecimal

/** Input command for a single product inside an ingestion batch. */
data class IngestProductCommand(
    val externalId: String,
    val nome: String,
    val preco: BigDecimal?,
)
