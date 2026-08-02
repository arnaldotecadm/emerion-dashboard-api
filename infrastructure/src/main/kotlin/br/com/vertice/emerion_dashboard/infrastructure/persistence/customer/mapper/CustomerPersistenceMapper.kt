package br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.mapper

import br.com.vertice.emerion_dashboard.domain.customer.model.Customer
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.model.CustomerJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customer.projection.CustomerProjection

/** Maps between the domain model and the JPA entity/read projection. Kept out of the entity/domain classes on purpose. */
object CustomerPersistenceMapper {

    /** Read path: native-query projection (see `CustomerQueryRepository`) -> domain model. */
    fun toDomain(projection: CustomerProjection): Customer =
        Customer(
            id = projection.id,
            externalId = projection.externalId,
            cnpjEmpresa = projection.cnpjEmpresa,
            nomeFantasia = projection.nomeFantasia,
            razaoSocial = projection.razaoSocial,
            cpfCnpj = projection.cpfCnpj,
            inscricaoEstadual = projection.inscricaoEstadual,
            regimeTributario = projection.regimeTributario,
            bloqueado = projection.bloqueado,
            dataNascimento = projection.dataNascimento,
            dataCadastro = projection.dataCadastro,
            dataUltimaAtualizacao = projection.dataUltimaAtualizacao,
            email1 = projection.email1,
            email2 = projection.email2,
            website = projection.website,
            limiteCredito = projection.limiteCredito,
            observacoes = projection.observacoes,
            cnae = projection.cnae,
            vendedorExternalId = projection.vendedorExternalId,
            nomeVendedor = projection.nomeVendedor,
            codigoTipoCliente = projection.codigoTipoCliente,
            codigoGrupoCliente = projection.codigoGrupoCliente,
            codigoCategoriaCliente = projection.codigoCategoriaCliente,
            createdAt = projection.createdAt,
            updatedAt = projection.updatedAt,
        )

    /** Write path: JPA entity -> domain model. */
    fun toDomain(entity: CustomerJpaEntity): Customer =
        Customer(
            id = entity.id,
            externalId = entity.externalId,
            cnpjEmpresa = entity.cnpjEmpresa,
            nomeFantasia = entity.nomeFantasia,
            razaoSocial = entity.razaoSocial,
            cpfCnpj = entity.cpfCnpj,
            inscricaoEstadual = entity.inscricaoEstadual,
            regimeTributario = entity.regimeTributario,
            bloqueado = entity.bloqueado,
            dataNascimento = entity.dataNascimento,
            dataCadastro = entity.dataCadastro,
            dataUltimaAtualizacao = entity.dataUltimaAtualizacao,
            email1 = entity.email1,
            email2 = entity.email2,
            website = entity.website,
            limiteCredito = entity.limiteCredito,
            observacoes = entity.observacoes,
            cnae = entity.cnae,
            vendedorExternalId = entity.vendedorExternalId,
            nomeVendedor = entity.nomeVendedor,
            codigoTipoCliente = entity.codigoTipoCliente,
            codigoGrupoCliente = entity.codigoGrupoCliente,
            codigoCategoriaCliente = entity.codigoCategoriaCliente,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun toEntity(domain: Customer, existing: CustomerJpaEntity?): CustomerJpaEntity =
        CustomerJpaEntity(
            id = existing?.id ?: domain.id,
            externalId = domain.externalId,
            cnpjEmpresa = domain.cnpjEmpresa,
            nomeFantasia = domain.nomeFantasia,
            razaoSocial = domain.razaoSocial,
            cpfCnpj = domain.cpfCnpj,
            inscricaoEstadual = domain.inscricaoEstadual,
            regimeTributario = domain.regimeTributario,
            bloqueado = domain.bloqueado,
            dataNascimento = domain.dataNascimento,
            dataCadastro = domain.dataCadastro,
            dataUltimaAtualizacao = domain.dataUltimaAtualizacao,
            email1 = domain.email1,
            email2 = domain.email2,
            website = domain.website,
            limiteCredito = domain.limiteCredito,
            observacoes = domain.observacoes,
            cnae = domain.cnae,
            vendedorExternalId = domain.vendedorExternalId,
            nomeVendedor = domain.nomeVendedor,
            codigoTipoCliente = domain.codigoTipoCliente,
            codigoGrupoCliente = domain.codigoGrupoCliente,
            codigoCategoriaCliente = domain.codigoCategoriaCliente,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )
}
