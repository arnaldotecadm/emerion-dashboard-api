package br.com.vertice.emerion_dashboard.application.customer.query.model

/** Input query for listing/filtering customers (paginated), consumed by the REST query adapter. */
data class ListCustomersQuery(
    val page: Int,
    val size: Int,
    val bloqueado: Boolean?,
    val nomeFantasiaContains: String?,
)
