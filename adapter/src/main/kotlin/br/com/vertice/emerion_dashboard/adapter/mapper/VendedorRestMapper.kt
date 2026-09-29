package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.domain.vendedor.Vendedor
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorResponse
import java.time.ZoneOffset

/** Maps the domain model to the generated OpenAPI response DTO. */
object VendedorRestMapper {

    fun Vendedor.toResponse(): VendedorResponse =
        VendedorResponse(
            id = id,
            externalId = externalId,
            cnpjEmpresa = cnpjEmpresa,
            nome = nome,
            apelido = apelido,
            cpfCnpj = cpfCnpj,
            telefone = telefone,
            celular = celular,
            email = email,
            cidade = cidade,
            uf = uf,
            ativo = ativo,
            saldo = saldo,
            dataCadastro = dataCadastro,
            createdAt = createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = updatedAt.atOffset(ZoneOffset.UTC),
        )
}
