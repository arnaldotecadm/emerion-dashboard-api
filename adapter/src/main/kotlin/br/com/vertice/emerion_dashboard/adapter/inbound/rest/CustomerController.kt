package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.application.customer.CustomerService
import br.com.vertice.emerion_dashboard.domain.customer.ListCustomersQuery
import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.CustomersApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneOffset

@RestController
class CustomerController(
    private val customerService: CustomerService,
) : CustomersApi {

    override fun getCustomerById(id: Long): ResponseEntity<CustomerResponse> {
        val customer = customerService.getById(id)
        return ResponseEntity.ok(toResponse(customer))
    }

    override fun getCustomerByExternalId(externalId: String): ResponseEntity<CustomerResponse> {
        val customer = customerService.getByExternalId(externalId)
        return ResponseEntity.ok(toResponse(customer))
    }

    override fun listCustomers(
        page: Int,
        size: Int,
        bloqueado: Boolean?,
        nomeFantasia: String?,
        cnpjEmpresa: String?,
    ): ResponseEntity<CustomerPage> {
        val query = ListCustomersQuery(
            page = page,
            size = size,
            bloqueado = bloqueado,
            nomeFantasiaContains = nomeFantasia,
            cnpjEmpresa = cnpjEmpresa,
        )
        val result = customerService.list(query)
        return ResponseEntity.ok(
            CustomerPage(
                data = result.content.map(::toResponse),
                pagination = PaginationInfo(
                    total = result.totalElements,
                    page = result.page,
                    propertySize = result.size,
                    totalPages = result.totalPages,
                ),
            ),
        )
    }

    private fun toResponse(customer: Customer): CustomerResponse =
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
}
