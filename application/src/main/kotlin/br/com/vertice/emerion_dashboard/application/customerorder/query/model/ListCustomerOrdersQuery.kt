package br.com.vertice.emerion_dashboard.application.customerorder.query.model

/**
 * Read-side filter/pagination parameters for listing customer orders,
 * translated from the REST query controller's request params.
 */
data class ListCustomerOrdersQuery(
    val page: Int,
    val size: Int,
    val codCli: String?,
    val sitres: String?,
    val cnpjEmpresa: String?,
)
