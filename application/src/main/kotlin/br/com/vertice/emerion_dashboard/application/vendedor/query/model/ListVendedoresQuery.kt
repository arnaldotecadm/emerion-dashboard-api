package br.com.vertice.emerion_dashboard.application.vendedor.query.model

/**
 * Read-side filter/pagination parameters for listing vendedores, translated
 * from the REST query controller's request params.
 */
data class ListVendedoresQuery(
    val page: Int,
    val size: Int,
    val nomeContains: String?,
    val situacao: String?,
    val cnpjEmpresa: String?,
)
