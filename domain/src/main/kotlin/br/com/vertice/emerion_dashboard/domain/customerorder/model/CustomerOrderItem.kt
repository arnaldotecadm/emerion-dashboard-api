package br.com.vertice.emerion_dashboard.domain.customerorder.model

import java.math.BigDecimal

/**
 * One line item of a CustomerOrder. A value object with no id of its own —
 * the whole `itens` list is sent on every re-ingestion, and each item is
 * upserted in its parent's persisted collection keyed by `seqRe2` (the
 * PEDRE2 line sequence, effectively this item's external id) rather than
 * `produto`, since the same produto can legitimately repeat across multiple
 * lines within one order (see CustomerOrder.mergeFromIngestion and
 * CustomerOrderPersistenceMapper.upsertItems).
 *
 * Fields beyond the original five (produto/descricao/quantidade/
 * valorUnitario/valorTotal) mirror the additional PEDRE2 fields exposed by
 * emerion-load-service's `CustomerOrderItemProjection` (legacy
 * ProcessadorDetalhePedido#getItemsPedidos query) — tax/fiscal breakdown,
 * discounts, and legacy tracking flags for the line item.
 */
data class CustomerOrderItem(
    val produto: String,
    val descricao: String? = null,
    val quantidade: BigDecimal,
    val valorUnitario: BigDecimal,
    val valorTotal: BigDecimal,
    val seqRe2: Int,
    val codClp: String? = null,
    val codSt1: String? = null,
    val codUnd: String? = null,
    val vluRe2: BigDecimal? = null,
    val dscRe2: BigDecimal? = null,
    val dsrRe2: BigDecimal? = null,
    val icmsAliquota: BigDecimal? = null,
    val icmsBase: BigDecimal? = null,
    val icmsValor: BigDecimal? = null,
    val icmsReducaoBase: BigDecimal? = null,
    val icmsSubstituicaoBase: BigDecimal? = null,
    val icmsSubstituicaoValor: BigDecimal? = null,
    val icmsSubstituicaoAliquota: BigDecimal? = null,
    val icmsSubstituicaoMargem: BigDecimal? = null,
    val icmsSubstituicaoReducaoBase: BigDecimal? = null,
    val ipiAliquota: BigDecimal? = null,
    val ipiBase: BigDecimal? = null,
    val ipiValor: BigDecimal? = null,
    val ipiClassificacao: String? = null,
    val ipiCst: String? = null,
    val pisBase: BigDecimal? = null,
    val pisAliquota: BigDecimal? = null,
    val pisValor: BigDecimal? = null,
    val pisCst: String? = null,
    val cofinsBase: BigDecimal? = null,
    val cofinsAliquota: BigDecimal? = null,
    val cofinsValor: BigDecimal? = null,
    val cofinsCst: String? = null,
    val descontoValor: BigDecimal? = null,
    val freteValor: BigDecimal? = null,
    val seguroValor: BigDecimal? = null,
    val outrasDespesasValor: BigDecimal? = null,
    val totalItemTributado: BigDecimal? = null,
    val totRen: BigDecimal? = null,
    val totGe2: BigDecimal? = null,
    val observacao: String? = null,
    val pedidoCompraCliente: String? = null,
    val itemPedidoCompraCliente: Int? = null,
    val nroRe2: Int? = null,
    val flgVal: String? = null,
    val flgPac: String? = null,
    val flgLib: String? = null,
    val codCfo: String? = null,
)
