package br.com.vertice.emerion_dashboard.infrastructure.rest.customeraddress.mapper

import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddress
import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddressDetail
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressDetailResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import java.time.ZoneOffset

/** Maps between the domain model and the generated OpenAPI query DTOs. */
object CustomerAddressQueryRestMapper {

    fun toResponse(customerAddress: CustomerAddress): CustomerAddressResponse =
        CustomerAddressResponse(
            id = customerAddress.id,
            externalId = customerAddress.externalId,
            cnpjEmpresa = customerAddress.cnpjEmpresa,
            cpfCnpj = customerAddress.cpfCnpj,
            enderecos = customerAddress.enderecos.map(::toDetailResponse),
            createdAt = customerAddress.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = customerAddress.updatedAt.atOffset(ZoneOffset.UTC),
        )

    private fun toDetailResponse(detail: CustomerAddressDetail): CustomerAddressDetailResponse =
        CustomerAddressDetailResponse(
            tipo = detail.tipo,
            cep = detail.cep,
            endereco = detail.endereco,
            numero = detail.numero,
            referencia = detail.referencia,
            bairro = detail.bairro,
            cidade = detail.cidade,
            uf = detail.uf,
            telefone = detail.telefone,
            telefoneContato = detail.telefoneContato,
            complemento = detail.complemento,
            fax = detail.fax,
        )

    fun toPageResponse(page: DomainPage<CustomerAddress>): CustomerAddressPage =
        CustomerAddressPage(
            data = page.content.map(::toResponse),
            pagination = PaginationInfo(
                total = page.totalElements,
                page = page.page,
                propertySize = page.size,
                totalPages = page.totalPages,
            ),
        )
}
