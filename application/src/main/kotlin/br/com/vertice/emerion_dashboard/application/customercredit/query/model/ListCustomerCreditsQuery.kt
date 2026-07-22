package br.com.vertice.emerion_dashboard.application.customercredit.query.model

/**
 * Read-side filter/pagination parameters for listing customer credit
 * ledger entries, translated from the REST query controller's request
 * params.
 */
data class ListCustomerCreditsQuery(
    val page: Int,
    val size: Int,
    val customerExternalId: String?,
    val tipo: String?,
)
