package br.com.vertice.emerion_dashboard.infrastructure.rest.customer

import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.domain.customer.CustomerStatus as DomainCustomerStatus
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerStatus as ApiCustomerStatus
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import java.time.ZoneOffset

/** Maps between the domain model and the generated OpenAPI query DTOs. */
object CustomerQueryRestMapper {

    fun toResponse(customer: Customer): CustomerResponse =
        CustomerResponse(
            id = customer.id,
            externalId = customer.externalId,
            name = customer.name,
            email = customer.email,
            status = toApiStatus(customer.status),
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

    fun toDomainStatus(status: ApiCustomerStatus?): DomainCustomerStatus? =
        when (status) {
            ApiCustomerStatus.ACTIVE -> DomainCustomerStatus.ACTIVE
            ApiCustomerStatus.INACTIVE -> DomainCustomerStatus.INACTIVE
            ApiCustomerStatus.UNKNOWN -> DomainCustomerStatus.UNKNOWN
            null -> null
        }

    private fun toApiStatus(status: DomainCustomerStatus): ApiCustomerStatus =
        when (status) {
            DomainCustomerStatus.ACTIVE -> ApiCustomerStatus.ACTIVE
            DomainCustomerStatus.INACTIVE -> ApiCustomerStatus.INACTIVE
            DomainCustomerStatus.UNKNOWN -> ApiCustomerStatus.UNKNOWN
        }
}
