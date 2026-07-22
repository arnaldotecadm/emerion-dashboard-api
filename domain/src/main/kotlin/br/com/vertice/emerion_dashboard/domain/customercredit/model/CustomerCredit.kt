package br.com.vertice.emerion_dashboard.domain.customercredit.model

import java.math.BigDecimal
import java.time.Instant

/**
 * Domain model for a single customer credit ledger entry. Plain Kotlin, no
 * JPA/Jakarta/OpenAPI annotations — this is what use cases and ports
 * operate on.
 *
 * Unlike Customer/Product, a credit entry has no independent externalId of
 * its own in the legacy schema: the upsert/idempotency key is the
 * combination of (customerExternalId, sequencia). When sequencia is null,
 * there is no reliable key and the entry is always inserted as new.
 */
data class CustomerCredit(
    val id: Long?,
    val customerExternalId: String,
    val sequencia: String?,
    val data: Instant,
    val dataPedido: Instant?,
    val valorUtilizado: BigDecimal,
    val valorTotal: BigDecimal,
    val saldo: BigDecimal,
    val situacao: String?,
    val tipo: String,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        /** Factory for a brand-new credit entry coming from ingestion (no id yet). */
        fun newFromIngestion(
            customerExternalId: String,
            sequencia: String?,
            data: Instant,
            dataPedido: Instant?,
            valorUtilizado: BigDecimal,
            valorTotal: BigDecimal,
            saldo: BigDecimal,
            situacao: String?,
            tipo: String,
            now: Instant,
        ) = CustomerCredit(
            id = null,
            customerExternalId = customerExternalId,
            sequencia = sequencia,
            data = data,
            dataPedido = dataPedido,
            valorUtilizado = valorUtilizado,
            valorTotal = valorTotal,
            saldo = saldo,
            situacao = situacao,
            tipo = tipo,
            createdAt = now,
            updatedAt = now,
        )
    }

    /** Applies an ingestion update on top of an existing credit entry, bumping updatedAt. */
    fun mergeFromIngestion(
        data: Instant,
        dataPedido: Instant?,
        valorUtilizado: BigDecimal,
        valorTotal: BigDecimal,
        saldo: BigDecimal,
        situacao: String?,
        tipo: String,
        now: Instant,
    ) = copy(
        data = data,
        dataPedido = dataPedido,
        valorUtilizado = valorUtilizado,
        valorTotal = valorTotal,
        saldo = saldo,
        situacao = situacao,
        tipo = tipo,
        updatedAt = now,
    )
}
