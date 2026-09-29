package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.mapper

import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.CustomerJpaEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.CustomerProjection

/** Maps between the domain model and the JPA entity/read projection. Kept out of the entity/domain classes on purpose. */
object CustomerPersistenceMapper {

    /** Read path: native-query projection (see `CustomerRepository`) -> domain model. */
    fun CustomerProjection.toDomain(): Customer =
        Customer(
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
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    /** Write path: JPA entity -> domain model. */
    fun CustomerJpaEntity.toDomain(): Customer =
        Customer(
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
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun Customer.toEntity(existing: CustomerJpaEntity?): CustomerJpaEntity =
        CustomerJpaEntity(
            id = existing?.id ?: id,
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
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
