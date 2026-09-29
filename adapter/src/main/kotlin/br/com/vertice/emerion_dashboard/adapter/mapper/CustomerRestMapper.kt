package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerResponse
import java.time.ZoneOffset

/** Maps the domain model to the generated OpenAPI response DTO. */
object CustomerRestMapper {

    fun Customer.toResponse(): CustomerResponse =
        CustomerResponse(
            id = id,
            externalId = externalId,
            cnpjEmpresa = cnpjEmpresa,
            nomeFantasia = nomeFantasia,
            razaoSocial = razaoSocial,
            cpfCnpj = cpfCnpj,
            inscricaoEstadual = inscricaoEstadual,
            regimeTributario = regimeTributario,
            bloqueado = bloqueado,
            dataNascimento = dataNascimento,
            dataCadastro = dataCadastro,
            dataUltimaAtualizacao = dataUltimaAtualizacao,
            email1 = email1,
            email2 = email2,
            website = website,
            limiteCredito = limiteCredito,
            observacoes = observacoes,
            cnae = cnae,
            vendedorExternalId = vendedorExternalId,
            nomeVendedor = nomeVendedor,
            codigoTipoCliente = codigoTipoCliente,
            codigoGrupoCliente = codigoGrupoCliente,
            codigoCategoriaCliente = codigoCategoriaCliente,
            uf = uf,
            macroRegiao = macroRegiao,
            microRegiao = microRegiao,
            setor = setor,
            createdAt = createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = updatedAt.atOffset(ZoneOffset.UTC),
        )
}
