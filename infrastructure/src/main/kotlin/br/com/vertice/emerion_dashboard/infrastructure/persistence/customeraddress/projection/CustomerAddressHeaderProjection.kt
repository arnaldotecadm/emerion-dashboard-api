package br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.projection

import java.time.Instant

/**
 * Read-side projection for the `customer_address` header row, populated
 * straight from a native SQL result set (see
 * `CustomerAddressQueryRepository`). Detail rows are fetched separately via
 * `CustomerAddressDetailProjection` and grouped back onto the header by
 * `id`/`customerAddressId`, mirroring emerion-load-service's
 * header-projection + item-projection split for one-to-many resources
 * (e.g. `CustomerOrderHeaderProjection`/`CustomerOrderItemProjection`).
 */
interface CustomerAddressHeaderProjection {
    val id: Long
    val externalId: String
    val cnpjEmpresa: String
    val cpfCnpj: String?
    val createdAt: Instant
    val updatedAt: Instant
}
