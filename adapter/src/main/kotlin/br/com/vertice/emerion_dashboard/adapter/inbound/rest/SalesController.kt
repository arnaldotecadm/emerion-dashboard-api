package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.DashboardMapper.toResponse
import br.com.vertice.emerion_dashboard.application.dashboard.DashboardSummaryService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.SalesApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrdersOverTime
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardTopTrendsDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class SalesController(
    private val dashboardSummaryService: DashboardSummaryService,
) : SalesApi {
    override fun getCustomerOrdersOverTime(
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ResponseEntity<List<CustomerOrdersOverTime>> =
        ResponseEntity.ok(
            dashboardSummaryService.getCustomerOrdersOverTime(
                startDate = startDate,
                endDate = endDate,
            ).map {
                CustomerOrdersOverTime(
                    yearMonth = it.yearMonth,
                    orderCount = it.orderCount,
                    amountSold = it.amountSold,
                )
            },
        )

    override fun getSellerTopTrends(
        orderby: String,
        years: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): ResponseEntity<List<DashboardTopTrendsDTO>> =
            ResponseEntity.ok(dashboardSummaryService.getSellerTopTrends(orderby, years, startDate, endDate).map { it.toResponse() })
}
