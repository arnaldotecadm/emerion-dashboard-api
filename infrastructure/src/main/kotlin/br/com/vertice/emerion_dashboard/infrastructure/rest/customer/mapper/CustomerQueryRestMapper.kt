package br.com.vertice.emerion_dashboard.infrastructure.rest.customer.mapper

import br.com.vertice.emerion_dashboard.domain.customer.model.Customer
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import java.time.ZoneOffset

/** Maps between the domain model and the generated OpenAPI query DTOs. */
object CustomerQueryRestMapper {

    fun toResponse(customer: Customer): CustomerResponse =
        CustomerResponse(
            id = customer.id,
            externalId = customer.externalId,
            nomeFantasia = customer.nomeFantasia,
            razaoSocial = customer.razaoSocial,
            cpfCnpj = customer.cpfCnpj,
            inscricaoEstadual = customer.inscricaoEstadual,
            regimeTributario = customer.regimeTributario,
            bloqueado = customer.bloqueado,
            createdAt = customer.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = customer.updatedAt.atOffset(ZoneOffset.UTC),
        )

    fun toPageResponse(page: DomainPage<Customer>): CustomerPage =
        CustomerPage(
            data = page.content.map(::toResponse),
            pagination = PaginationInfo(
                total = page.totalElements,
                page = page.page,
                propertySize = page.size,
                totalPages = page.totalPages,
            ),
        )
}
