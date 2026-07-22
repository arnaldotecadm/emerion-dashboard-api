package br.com.vertice.emerion_dashboard.domain.customeraddress.model

import java.time.Instant

/**
 * Domain model for a customer's full address set (header + detail rows).
 * Plain Kotlin, no JPA/Jakarta/OpenAPI annotations — this is what use cases
 * and ports operate on.
 */
data class CustomerAddress(
    val id: Long?,
    val externalId: String,
    val cnpjEmpresa: String?,
    val cpfCnpj: String?,
    val enderecos: List<CustomerAddressDetail>,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        /** Factory for a brand-new address set coming from ingestion (no id yet). */
        fun newFromIngestion(
            externalId: String,
            cnpjEmpresa: String?,
            cpfCnpj: String?,
            enderecos: List<CustomerAddressDetail>,
            now: Instant,
        ) = CustomerAddress(
            id = null,
            externalId = externalId,
            cnpjEmpresa = cnpjEmpresa,
            cpfCnpj = cpfCnpj,
            enderecos = enderecos,
            createdAt = now,
            updatedAt = now,
        )
    }

    /**
     * Applies an ingestion update on top of an existing address set, bumping
     * updatedAt. The `enderecos` list is replaced wholesale, matching the
     * source system's contract of always sending the customer's complete
     * address set in a single payload.
     */
    fun mergeFromIngestion(
        cnpjEmpresa: String?,
        cpfCnpj: String?,
        enderecos: List<CustomerAddressDetail>,
        now: Instant,
    ) = copy(
        cnpjEmpresa = cnpjEmpresa,
        cpfCnpj = cpfCnpj,
        enderecos = enderecos,
        updatedAt = now,
    )
}
