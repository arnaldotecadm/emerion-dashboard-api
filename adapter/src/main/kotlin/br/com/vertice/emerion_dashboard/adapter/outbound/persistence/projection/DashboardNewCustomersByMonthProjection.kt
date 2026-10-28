package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection

data class DashboardNewCustomersByMonthProjection(
    val yearMonth: String,
    val count: Int,
)
