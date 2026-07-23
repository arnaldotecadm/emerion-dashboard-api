package br.com.vertice.emerion_dashboard.application.customer.ingestion.model

import java.time.Instant

/** Input command for a single customer inside an ingestion batch. */
data class IngestCustomerCommand(
    val externalId: String,
    val cnpjEmpresa: String,
    val nomeFantasia: String,
    val razaoSocial: String,
    val cpfCnpj: String,
    val inscricaoEstadual: String?,
    val regimeTributario: String?,
    val bloqueado: Boolean,
    val createdAt: Instant?,
)
