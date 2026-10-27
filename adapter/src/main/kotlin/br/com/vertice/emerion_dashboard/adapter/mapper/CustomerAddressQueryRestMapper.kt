package br.com.vertice.emerion_dashboard.adapter.mapper

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

    fun CustomerAddress.toResponse(): CustomerAddressResponse =
        CustomerAddressResponse(
            id = id,
            externalId = externalId,
            cnpjEmpresa = cnpjEmpresa,
            cpfCnpj = cpfCnpj,
            enderecos = enderecos.map { it.toDetailResponse() },
            createdAt = createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = updatedAt.atOffset(ZoneOffset.UTC),
        )

    private fun CustomerAddressDetail.toDetailResponse(): CustomerAddressDetailResponse =
        CustomerAddressDetailResponse(
            tipo = tipo,
            cep = cep,
            endereco = endereco,
            numero = numero,
            referencia = referencia,
            bairro = bairro,
            cidade = cidade,
            uf = uf,
            telefone = telefone,
            telefoneContato = telefoneContato,
            complemento = complemento,
            fax = fax,
            tipoEndereco = tipoEndereco,
            dddTelefone = dddTelefone,
            dddFax = dddFax,
            dddCelular = dddCelular,
            celular = celular,
        )

    fun DomainPage<CustomerAddress>.toPageResponse(): CustomerAddressPage =
        CustomerAddressPage(
            data = content.map { it.toResponse() },
            pagination = PaginationInfo(
                total = totalElements,
                page = page,
                propertySize = size,
                totalPages = totalPages,
            ),
        )
}
