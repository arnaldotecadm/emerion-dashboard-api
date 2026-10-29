package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardTopTrendsProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardTotaisProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardMonthlyTrendProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardOrdersByRegionProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardCustomerRegionProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardNewCustomersByMonthProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardOverdueInvoiceProjection
import br.com.vertice.emerion_dashboard.domain.dashboard.OverdueInvoice
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardCockpitMetricsProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardRecentOrderProjection
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.DashboardUnpaidInvoicesProjection
import br.com.vertice.emerion_dashboard.domain.dashboard.CockpitMonthlyMetrics
import br.com.vertice.emerion_dashboard.domain.dashboard.RecentOrder
import br.com.vertice.emerion_dashboard.domain.dashboard.SalesCockpit
import br.com.vertice.emerion_dashboard.domain.dashboard.UnpaidInvoicesSummary
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CockpitRecentOrder
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.SalesCockpitResponse
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyTrend
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardMonthlyCount
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardOrdersByRegion
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTopTrends
import br.com.vertice.emerion_dashboard.domain.dashboard.DashboardTotais
import br.com.vertice.emerion_dashboard.domain.dashboard.CustomerRegionDistribution
import br.com.vertice.emerion_dashboard.domain.dashboard.TopTrends
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardTopTrendsDTO
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardMonthlyCount as DashboardMonthlyCountDto
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardOrdersByRegion as DashboardOrdersByRegionDto
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.DashboardTopTrendsResponse

object DashboardMapper {

    fun DashboardCockpitMetricsProjection.toModel(): CockpitMonthlyMetrics =
        CockpitMonthlyMetrics(
            salesTotal = salesTotal,
            averageMarkup = averageMarkup,
            customerOrders = customerOrders,
            customerQuotations = customerQuotations,
            amountToReceive = amountToReceive,
            amountReceived = amountReceived,
        )

    fun DashboardUnpaidInvoicesProjection.toModel(): UnpaidInvoicesSummary =
        UnpaidInvoicesSummary(count = count, amount = amount)

    fun DashboardRecentOrderProjection.toModel(): RecentOrder =
        RecentOrder(
            orderNumber = orderNumber,
            customerCode = customerCode,
            customerName = customerName,
            orderDate = orderDate,
            status = status,
            totalAmount = totalAmount,
            salesmanName = salesmanName,
            averageMarkup = averageMarkup,
        )

    fun SalesCockpit.toResponse(): SalesCockpitResponse =
        SalesCockpitResponse(
            monthlySalesTotal = monthlySalesTotal,
            averageMarkup = averageMarkup,
            unpaidInvoicesCount = unpaidInvoicesCount,
            unpaidInvoicesAmount = unpaidInvoicesAmount,
            customerQuotations = customerQuotations,
            customerOrders = customerOrders,
            invoices = invoices,
            amountToReceiveThisMonth = amountToReceiveThisMonth,
            amountReceivedThisMonth = amountReceivedThisMonth,
            amountDueThisMonth = amountDueThisMonth,
            recentOrders = recentOrders.map {
                CockpitRecentOrder(
                    orderNumber = it.orderNumber,
                    customerCode = it.customerCode,
                    customerName = it.customerName,
                    orderDate = it.orderDate,
                    status = it.status,
                    totalAmount = it.totalAmount,
                    salesmanName = it.salesmanName,
                    averageMarkup = it.averageMarkup,
                )
            },
        )

    fun DashboardOverdueInvoiceProjection.toModel(): OverdueInvoice =
        OverdueInvoice(
            customerCode = customerCode,
            customerName = customerName,
            amount = amount,
            dueDate = dueDate,
            daysOverdue = daysOverdue,
        )

    fun DashboardCustomerRegionProjection.toModel(): CustomerRegionDistribution =
        CustomerRegionDistribution(region = region, customerCount = customerCount)

    fun DashboardNewCustomersByMonthProjection.toModel(): DashboardMonthlyCount =
        DashboardMonthlyCount(yearMonth = yearMonth, count = count)

    fun DashboardOrdersByRegionProjection.toModel(): DashboardOrdersByRegion =
        DashboardOrdersByRegion(
            region = region,
            faturado = faturado,
            cancelado = cancelado,
            outros = outros,
        )

    fun DashboardMonthlyTrendProjection.toModel(): DashboardMonthlyTrend =
        DashboardMonthlyTrend(
            yearMonth = yearMonth,
            customerOrders = customerOrders,
            customerOrdersAmountSold = customerOrdersAmountSold,
            customerQuotations = customerQuotations,
        )

    fun DashboardTopTrends.toResponse(): DashboardTopTrendsResponse {
        return DashboardTopTrendsResponse(
            topCustomers = this.topCustomers.map { it.toResponse() },
            topVendedores = this.topVendedores.map { it.toResponse() },
            customerOrders = this.customerOrders.map {
                DashboardMonthlyCountDto(yearMonth = it.yearMonth, count = it.count)
            },
            customerQuotations = this.customerQuotations.map {
                DashboardMonthlyCountDto(yearMonth = it.yearMonth, count = it.count)
            },
            ordersByRegion = this.ordersByRegion.map {
                DashboardOrdersByRegionDto(
                    region = it.region,
                    faturado = it.faturado,
                    cancelado = it.cancelado,
                    outros = it.outros,
                )
            },
        )
    }

    fun TopTrends.toResponse(): DashboardTopTrendsDTO {
        return DashboardTopTrendsDTO(
            identifier = this.identifier,
            name = this.name,
            amountSales = this.amountSales,
            totalSales = this.totalSales
        )
    }

    fun DashboardTopTrendsProjection.toModel(): TopTrends {
        return TopTrends(
            identifier = this.identifier.toInt(),
            name = this.name,
            amountSales = this.amountSales,
            totalSales = this.totalSales
        )
    }

    fun DashboardTotaisProjection.toModel(): DashboardTotais {
        return DashboardTotais(
            quantidadePedidos = this.quantidadePedidos,
            quantidadeClientes = this.quantidadeClientes,
            quantidadeVendedores = this.quantidadeVendedores,
            totalFaturado = this.totalFaturado
        )
    }
}