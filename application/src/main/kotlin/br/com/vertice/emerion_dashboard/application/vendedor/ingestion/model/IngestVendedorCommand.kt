package br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model

import java.math.BigDecimal
import java.time.LocalDate

/** Input command for a single vendedor inside an ingestion batch. */
data class IngestVendedorCommand(
    val externalId: String,
    val nome: String,
    val apelido: String?,
    val cpfCnpj: String?,
    val telefone: String?,
    val celular: String?,
    val email: String?,
    val cidade: String?,
    val uf: String?,
    val situacao: String?,
    val saldo: BigDecimal?,
    val dataCadastro: LocalDate?,
)
