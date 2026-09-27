package br.com.vertice.emerion_dashboard.application.dashboard

import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTopTrends

interface DashboardSummary {
    fun getDashboardTopTrends(orderBy: String): DashboardTopTrends
}