package br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model

import java.math.BigDecimal
import java.time.LocalDate

/** Input command for a single customer order inside an ingestion batch. */
data class IngestCustomerOrderCommand(
    val externalId: String,
    val codigoEmpresa: Int,
    val codigoCliente: Int,
    val cpfCnpj: String?,
    val numeroPedido: String,
    val dataPedido: LocalDate,
    val statusPedido: String?,
    val totalPedidoComImpostos: BigDecimal,
    val totalPedidoSemImpostos: BigDecimal,
    val totalIpi: BigDecimal,
    val totalIcms: BigDecimal,
    val totalPis: BigDecimal,
    val totalCofins: BigDecimal,
    val totalSubstituicaoTributaria: BigDecimal,
    val totalDescontoIncondicional: BigDecimal,
    val totalFrete: BigDecimal? = null,
    val totalSeguro: BigDecimal? = null,
    val totalOutrasDespesas: BigDecimal? = null,
    val vendedorExternalId: Long? = null,
    val dataEntregaPrevista: LocalDate? = null,
    val codigoTransportadora: String? = null,
    val pedidoAnterior: String? = null,
    val regimeTributario: String? = null,
    val nomeRegimeTributario: String? = null,
    val codigoPadraoFaturamento: String? = null,
    val itens: List<IngestCustomerOrderItemCommand>,
)
