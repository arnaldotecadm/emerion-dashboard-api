package br.com.vertice.emerion_dashboard.domain.vendedor

/** Input query for listing/filtering vendedores (paginated), consumed by the REST query adapter. */
data class ListVendedoresQuery(
    val page: Int,
    val size: Int,
    val nomeContains: String?,
    val situacao: String?,
    val cnpjEmpresa: String?,
)
