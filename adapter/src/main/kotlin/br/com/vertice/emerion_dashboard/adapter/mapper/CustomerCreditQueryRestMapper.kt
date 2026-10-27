package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.domain.customercredit.model.CustomerCredit
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerCreditPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerCreditResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import java.time.ZoneOffset

/** Maps between the domain model and the generated OpenAPI query DTOs. */
object CustomerCreditQueryRestMapper {

    fun CustomerCredit.toResponse(): CustomerCreditResponse =
        CustomerCreditResponse(
            id = id,
            customerExternalId = customerExternalId,
            cnpjEmpresa = cnpjEmpresa,
            sequencia = sequencia,
            data = data,
            dataPedido = dataPedido,
            valorUtilizado = valorUtilizado,
            valorTotal = valorTotal,
            saldo = saldo,
            situacao = situacao,
            tipo = tipo,
            createdAt = createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = updatedAt.atOffset(ZoneOffset.UTC),
        )

    fun DomainPage<CustomerCredit>.toPageResponse(): CustomerCreditPage =
        CustomerCreditPage(
            data = content.map { it.toResponse() },
            pagination = PaginationInfo(
                total = totalElements,
                page = page,
                propertySize = size,
                totalPages = totalPages,
            ),
        )
}
