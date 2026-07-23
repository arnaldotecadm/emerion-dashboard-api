package br.com.vertice.emerion_dashboard.application.product.query.model

/**
 * Read-side filter/pagination parameters for listing products, translated
 * from the REST query controller's request params.
 */
data class ListProductsQuery(
    val page: Int,
    val size: Int,
    val nomeContains: String?,
    val cnpjEmpresa: String?,
)
