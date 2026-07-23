package br.com.vertice.emerion_dashboard.infrastructure.rest.customercredit.mapper

import br.com.vertice.emerion_dashboard.domain.customercredit.model.CustomerCredit
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerCreditPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerCreditResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import java.time.ZoneOffset

/** Maps between the domain model and the generated OpenAPI query DTOs. */
object CustomerCreditQueryRestMapper {

    fun toResponse(customerCredit: CustomerCredit): CustomerCreditResponse =
        CustomerCreditResponse(
            id = customerCredit.id,
            customerExternalId = customerCredit.customerExternalId,
            cnpjEmpresa = customerCredit.cnpjEmpresa,
            sequencia = customerCredit.sequencia,
            data = customerCredit.data.atOffset(ZoneOffset.UTC),
            dataPedido = customerCredit.dataPedido?.atOffset(ZoneOffset.UTC),
            valorUtilizado = customerCredit.valorUtilizado,
            valorTotal = customerCredit.valorTotal,
            saldo = customerCredit.saldo,
            situacao = customerCredit.situacao,
            tipo = customerCredit.tipo,
            createdAt = customerCredit.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = customerCredit.updatedAt.atOffset(ZoneOffset.UTC),
        )

    fun toPageResponse(page: DomainPage<CustomerCredit>): CustomerCreditPage =
        CustomerCreditPage(
            data = page.content.map(::toResponse),
            pagination = PaginationInfo(
                total = page.totalElements,
                page = page.page,
                propertySize = page.size,
                totalPages = page.totalPages,
            ),
        )
}
