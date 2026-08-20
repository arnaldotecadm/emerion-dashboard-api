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
            cnpjEmpresa = customer.cnpjEmpresa,
            nomeFantasia = customer.nomeFantasia,
            razaoSocial = customer.razaoSocial,
            cpfCnpj = customer.cpfCnpj,
            inscricaoEstadual = customer.inscricaoEstadual,
            regimeTributario = customer.regimeTributario,
            bloqueado = customer.bloqueado,
            dataNascimento = customer.dataNascimento,
            dataCadastro = customer.dataCadastro,
            dataUltimaAtualizacao = customer.dataUltimaAtualizacao,
            email1 = customer.email1,
            email2 = customer.email2,
            website = customer.website,
            limiteCredito = customer.limiteCredito,
            observacoes = customer.observacoes,
            cnae = customer.cnae,
            vendedorExternalId = customer.vendedorExternalId,
            nomeVendedor = customer.nomeVendedor,
            codigoTipoCliente = customer.codigoTipoCliente,
            codigoGrupoCliente = customer.codigoGrupoCliente,
            codigoCategoriaCliente = customer.codigoCategoriaCliente,
            uf = customer.uf,
            macroRegiao = customer.macroRegiao,
            microRegiao = customer.microRegiao,
            setor = customer.setor,
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
