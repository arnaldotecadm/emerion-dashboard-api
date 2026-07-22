package br.com.vertice.emerion_dashboard.application.customeraddress.query.model

/**
 * Read-side filter/pagination parameters for listing customer address sets,
 * translated from the REST query controller's request params.
 */
data class ListCustomerAddressesQuery(
    val page: Int,
    val size: Int,
    val cpfCnpjContains: String?,
)
