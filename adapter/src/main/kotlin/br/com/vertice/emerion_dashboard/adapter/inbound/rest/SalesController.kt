package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.application.dashboard.DashboardSummaryService
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.SalesApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrdersOverTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SalesController(
    private val dashboardSummaryService: DashboardSummaryService,
) : SalesApi {
    override fun getCustomerOrdersOverTime(): ResponseEntity<List<CustomerOrdersOverTime>> =
        ResponseEntity.ok(
            dashboardSummaryService.getCustomerOrdersOverTime().map {
                CustomerOrdersOverTime(
                    yearMonth = it.yearMonth,
                    orderCount = it.orderCount,
                    amountSold = it.amountSold,
                )
            },
        )
}
