package br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.projection

/**
 * Read-side projection for one `customer_address_detail` row, fetched
 * alongside `CustomerAddressHeaderProjection` and grouped back onto its
 * parent header by `customerAddressId` (see `CustomerAddressQueryRepository`).
 */
interface CustomerAddressDetailProjection {
    val customerAddressId: Long
    val tipo: String
    val cep: String?
    val endereco: String?
    val numero: String?
    val referencia: String?
    val bairro: String?
    val cidade: String?
    val uf: String?
    val telefone: String?
    val telefoneContato: String?
    val complemento: String?
    val fax: String?
}
