package br.com.vertice.emerion_dashboard.application.dashboard

import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTopTrends
import java.time.LocalDate

interface DashboardSummary {
    fun getDashboardTopTrends(
        orderBy: String,
        years: Int? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): DashboardTopTrends
}