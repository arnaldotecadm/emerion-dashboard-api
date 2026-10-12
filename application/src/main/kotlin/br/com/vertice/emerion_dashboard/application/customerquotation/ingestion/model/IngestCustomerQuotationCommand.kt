package br.com.vertice.emerion_dashboard.application.customerquotation.ingestion.model

import java.math.BigDecimal
import java.time.LocalDate

data class IngestCustomerQuotationCommand(
    val externalId: String,
    val cnpjEmpresa: String,
    val codigoEmpresa: Int,
    val codigoCliente: Int,
    val customerExternalId: Long,
    val dteres: LocalDate,
    val numeroPedido: String,
    val dataPedido: LocalDate,
    val totalPedidoComImpostos: BigDecimal,
    val totalPedidoSemImpostos: BigDecimal,
    val cpfCnpj: String?,
    val statusPedido: String?,
    val totalIpi: BigDecimal,
    val totalIcms: BigDecimal,
    val totalPis: BigDecimal?,
    val totalCofins: BigDecimal?,
    val totalSubstituicaoTributaria: BigDecimal,
    val totalDescontoIncondicional: BigDecimal?,
    val totalFrete: BigDecimal?,
    val totalSeguro: BigDecimal?,
    val totalOutrasDespesas: BigDecimal?,
    val vendedorExternalId: Long?,
    val atendenteCod: String?,
    val regimeTributario: String?,
    val nomeRegimeTributario: String?,
    val codigoPadraoFaturamento: String?,
    val numeroCotacao: Int?,
)
