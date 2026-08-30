package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.projection

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

/**
 * Read-side projection for the `customer_order` header row, populated
 * straight from a native SQL result set (see
 * `CustomerOrderQueryRepository`). Line items are fetched separately via
 * `CustomerOrderItemProjection` and grouped back onto the header by
 * `id`/`customerOrderId`, mirroring emerion-load-service's
 * `CustomerOrderHeaderProjection`/`CustomerOrderItemProjection` split.
 */
interface CustomerOrderHeaderProjection {
    val id: Long
    val externalId: String
    val codigoEmpresa: Int
    val codigoCliente: Int
    val cpfCnpj: String?
    val numeroPedido: String
    val dataPedido: LocalDate
    val statusPedido: String?
    val totalPedidoComImpostos: BigDecimal
    val totalPedidoSemImpostos: BigDecimal
    val totalIpi: BigDecimal
    val totalIcms: BigDecimal
    val totalPis: BigDecimal
    val totalCofins: BigDecimal
    val totalSubstituicaoTributaria: BigDecimal
    val totalDescontoIncondicional: BigDecimal
    val totalFrete: BigDecimal?
    val totalSeguro: BigDecimal?
    val totalOutrasDespesas: BigDecimal?
    val vendedorExternalId: Long?
    val dataEntregaPrevista: LocalDate?
    val codigoTransportadora: String?
    val pedidoAnterior: String?
    val regimeTributario: String?
    val nomeRegimeTributario: String?
    val codigoPadraoFaturamento: String?
    val createdAt: Instant
    val updatedAt: Instant
}
