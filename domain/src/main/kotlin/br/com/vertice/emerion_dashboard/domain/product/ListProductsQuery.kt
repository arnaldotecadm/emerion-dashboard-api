package br.com.vertice.emerion_dashboard.domain.product

/** Input query for listing/filtering products (paginated), consumed by the REST query adapter. */
data class ListProductsQuery(
    val page: Int,
    val size: Int,
    val nomeContains: String?,
    val cnpjEmpresa: String?,
)
