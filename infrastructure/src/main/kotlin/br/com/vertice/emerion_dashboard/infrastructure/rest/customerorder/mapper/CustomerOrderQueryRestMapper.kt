package br.com.vertice.emerion_dashboard.infrastructure.rest.customerorder.mapper

import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrder
import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrderItem
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderItemResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import java.time.ZoneOffset

/** Maps between the domain model and the generated OpenAPI query DTOs. */
object CustomerOrderQueryRestMapper {

    fun toResponse(customerOrder: CustomerOrder): CustomerOrderResponse =
        CustomerOrderResponse(
            id = customerOrder.id,
            externalId = customerOrder.externalId,
            codCli = customerOrder.codCli,
            cnpjEmpresa = customerOrder.cnpjEmpresa,
            nronfe = customerOrder.nronfe,
            dteres = customerOrder.dteres.atOffset(ZoneOffset.UTC),
            sitres = customerOrder.sitres,
            totger = customerOrder.totger,
            totres = customerOrder.totres,
            totipi = customerOrder.totipi,
            totsub = customerOrder.totsub,
            totdescinc = customerOrder.totdescinc,
            itens = customerOrder.itens.map(::toItemResponse),
            createdAt = customerOrder.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = customerOrder.updatedAt.atOffset(ZoneOffset.UTC),
        )

    private fun toItemResponse(item: CustomerOrderItem): CustomerOrderItemResponse =
        CustomerOrderItemResponse(
            produto = item.produto,
            descricao = item.descricao,
            quantidade = item.quantidade,
            valorUnitario = item.valorUnitario,
            valorTotal = item.valorTotal,
        )

    fun toPageResponse(page: DomainPage<CustomerOrder>): CustomerOrderPage =
        CustomerOrderPage(
            data = page.content.map(::toResponse),
            pagination = PaginationInfo(
                total = page.totalElements,
                page = page.page,
                propertySize = page.size,
                totalPages = page.totalPages,
            ),
        )
}
