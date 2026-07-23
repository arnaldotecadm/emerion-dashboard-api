package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.projection

import java.math.BigDecimal

/**
 * Read-side projection for one `customer_order_item` row, fetched alongside
 * `CustomerOrderHeaderProjection` and grouped back onto its parent header
 * by `customerOrderId` (see `CustomerOrderQueryRepository`).
 */
interface CustomerOrderItemProjection {
    val customerOrderId: Long
    val produto: String
    val descricao: String?
    val quantidade: BigDecimal
    val valorUnitario: BigDecimal
    val valorTotal: BigDecimal
    val seqRe2: Int
    val codClp: String?
    val codSt1: String?
    val codUnd: String?
    val vluRe2: BigDecimal?
    val dscRe2: BigDecimal?
    val dsrRe2: BigDecimal?
    val icmsAliquota: BigDecimal?
    val icmsBase: BigDecimal?
    val icmsValor: BigDecimal?
    val icmsReducaoBase: BigDecimal?
    val icmsSubstituicaoBase: BigDecimal?
    val icmsSubstituicaoValor: BigDecimal?
    val icmsSubstituicaoAliquota: BigDecimal?
    val icmsSubstituicaoMargem: BigDecimal?
    val icmsSubstituicaoReducaoBase: BigDecimal?
    val ipiAliquota: BigDecimal?
    val ipiBase: BigDecimal?
    val ipiValor: BigDecimal?
    val ipiClassificacao: String?
    val ipiCst: String?
    val pisBase: BigDecimal?
    val pisAliquota: BigDecimal?
    val pisValor: BigDecimal?
    val pisCst: String?
    val cofinsBase: BigDecimal?
    val cofinsAliquota: BigDecimal?
    val cofinsValor: BigDecimal?
    val cofinsCst: String?
    val descontoValor: BigDecimal?
    val freteValor: BigDecimal?
    val seguroValor: BigDecimal?
    val outrasDespesasValor: BigDecimal?
    val totalItemTributado: BigDecimal?
    val totRen: BigDecimal?
    val totGe2: BigDecimal?
    val observacao: String?
    val pedidoCompraCliente: String?
    val itemPedidoCompraCliente: Int?
    val nroRe2: Int?
    val flgVal: String?
    val flgPac: String?
    val flgLib: String?
    val codCfo: String?
}
