package br.com.vertice.emerion_dashboard.domain.customerorder.model

import java.math.BigDecimal
import java.time.Instant

/**
 * Domain model for a customer order (header + line items). Plain Kotlin, no
 * JPA/Jakarta/OpenAPI annotations — this is what use cases and ports
 * operate on.
 */
data class CustomerOrder(
    val id: Long?,
    val externalId: String,
    val codCli: String,
    val cnpjEmpresa: String?,
    val nronfe: String?,
    val dteres: Instant,
    val sitres: String?,
    val totger: BigDecimal,
    val totres: BigDecimal,
    val totipi: BigDecimal,
    val totsub: BigDecimal,
    val totdescinc: BigDecimal,
    val itens: List<CustomerOrderItem>,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        /** Factory for a brand-new order coming from ingestion (no id yet). */
        fun newFromIngestion(
            externalId: String,
            codCli: String,
            cnpjEmpresa: String?,
            nronfe: String?,
            dteres: Instant,
            sitres: String?,
            totger: BigDecimal,
            totres: BigDecimal,
            totipi: BigDecimal,
            totsub: BigDecimal,
            totdescinc: BigDecimal,
            itens: List<CustomerOrderItem>,
            now: Instant,
        ) = CustomerOrder(
            id = null,
            externalId = externalId,
            codCli = codCli,
            cnpjEmpresa = cnpjEmpresa,
            nronfe = nronfe,
            dteres = dteres,
            sitres = sitres,
            totger = totger,
            totres = totres,
            totipi = totipi,
            totsub = totsub,
            totdescinc = totdescinc,
            itens = itens,
            createdAt = now,
            updatedAt = now,
        )
    }

    /**
     * Applies an ingestion update on top of an existing order, bumping
     * updatedAt. The `itens` list is replaced wholesale, matching the
     * source system's contract of always sending the order's complete line
     * items in a single payload.
     */
    fun mergeFromIngestion(
        codCli: String,
        cnpjEmpresa: String?,
        nronfe: String?,
        dteres: Instant,
        sitres: String?,
        totger: BigDecimal,
        totres: BigDecimal,
        totipi: BigDecimal,
        totsub: BigDecimal,
        totdescinc: BigDecimal,
        itens: List<CustomerOrderItem>,
        now: Instant,
    ) = copy(
        codCli = codCli,
        cnpjEmpresa = cnpjEmpresa,
        nronfe = nronfe,
        dteres = dteres,
        sitres = sitres,
        totger = totger,
        totres = totres,
        totipi = totipi,
        totsub = totsub,
        totdescinc = totdescinc,
        itens = itens,
        updatedAt = now,
    )
}
