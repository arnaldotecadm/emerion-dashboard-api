package br.com.vertice.emerion_dashboard.infrastructure.rest.vendedor.mapper

import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.domain.vendedor.model.Vendedor
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorResponse
import java.time.ZoneOffset

/** Maps between the domain model and the generated OpenAPI query DTOs. */
object VendedorQueryRestMapper {

    fun toResponse(vendedor: Vendedor): VendedorResponse =
        VendedorResponse(
            id = vendedor.id,
            externalId = vendedor.externalId,
            nome = vendedor.nome,
            apelido = vendedor.apelido,
            cpfCnpj = vendedor.cpfCnpj,
            telefone = vendedor.telefone,
            celular = vendedor.celular,
            email = vendedor.email,
            cidade = vendedor.cidade,
            uf = vendedor.uf,
            situacao = vendedor.situacao,
            saldo = vendedor.saldo,
            dataCadastro = vendedor.dataCadastro,
            createdAt = vendedor.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = vendedor.updatedAt.atOffset(ZoneOffset.UTC),
        )

    fun toPageResponse(page: DomainPage<Vendedor>): VendedorPage =
        VendedorPage(
            data = page.content.map(::toResponse),
            pagination = PaginationInfo(
                total = page.totalElements,
                page = page.page,
                propertySize = page.size,
                totalPages = page.totalPages,
            ),
        )
}
