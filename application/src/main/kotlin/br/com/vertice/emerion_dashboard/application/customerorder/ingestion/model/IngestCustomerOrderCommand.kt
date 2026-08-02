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
    val totfrt: BigDecimal? = null,
    val totseg: BigDecimal? = null,
    val totoutdesp: BigDecimal? = null,
    val vendedorExternalId: Long? = null,
    val atendenteCod: String? = null,
    val dataEntregaPrevista: LocalDate? = null,
    val descontoComercial: BigDecimal? = null,
    val descontoRegional: BigDecimal? = null,
    val codigoTransportadora: String? = null,
    val linhaReserva: String? = null,
    val pedidoAnterior: String? = null,
    val regimeTributario: String? = null,
    val nomeRegimeTributario: String? = null,
    val itens: List<IngestCustomerOrderItemCommand>,
)
