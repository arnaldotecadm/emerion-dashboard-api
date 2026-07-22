package br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.mapper

import br.com.vertice.emerion_dashboard.domain.vendedor.model.Vendedor
import br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.model.VendedorJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.projection.VendedorProjection

/** Maps between the domain model and the JPA entity/read projection. Kept out of the entity/domain classes on purpose. */
object VendedorPersistenceMapper {

    /** Read path: native-query projection (see `VendedorQueryRepository`) -> domain model. */
    fun toDomain(projection: VendedorProjection): Vendedor =
        Vendedor(
            id = projection.id,
            externalId = projection.externalId,
            nome = projection.nome,
            apelido = projection.apelido,
            cpfCnpj = projection.cpfCnpj,
            telefone = projection.telefone,
            celular = projection.celular,
            email = projection.email,
            cidade = projection.cidade,
            uf = projection.uf,
            situacao = projection.situacao,
            saldo = projection.saldo,
            dataCadastro = projection.dataCadastro,
            createdAt = projection.createdAt,
            updatedAt = projection.updatedAt,
        )

    /** Write path: JPA entity -> domain model. */
    fun toDomain(entity: VendedorJpaEntity): Vendedor =
        Vendedor(
            id = entity.id,
            externalId = entity.externalId,
            nome = entity.nome,
            apelido = entity.apelido,
            cpfCnpj = entity.cpfCnpj,
            telefone = entity.telefone,
            celular = entity.celular,
            email = entity.email,
            cidade = entity.cidade,
            uf = entity.uf,
            situacao = entity.situacao,
            saldo = entity.saldo,
            dataCadastro = entity.dataCadastro,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun toEntity(domain: Vendedor, existing: VendedorJpaEntity?): VendedorJpaEntity =
        VendedorJpaEntity(
            id = existing?.id ?: domain.id,
            externalId = domain.externalId,
            nome = domain.nome,
            apelido = domain.apelido,
            cpfCnpj = domain.cpfCnpj,
            telefone = domain.telefone,
            celular = domain.celular,
            email = domain.email,
            cidade = domain.cidade,
            uf = domain.uf,
            situacao = domain.situacao,
            saldo = domain.saldo,
            dataCadastro = domain.dataCadastro,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )
}
