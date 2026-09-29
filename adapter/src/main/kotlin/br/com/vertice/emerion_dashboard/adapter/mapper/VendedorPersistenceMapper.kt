package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.VendedorJpaEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.projection.VendedorProjection
import br.com.vertice.emerion_dashboard.domain.vendedor.Vendedor

/** Maps between the domain model and the JPA entity/read projection. Kept out of the entity/domain classes on purpose. */
object VendedorPersistenceMapper {

    /** Read path: native-query projection (see `VendedorRepository`) -> domain model. */
    fun VendedorProjection.toDomain(): Vendedor =
        Vendedor(
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
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    /** Write path: JPA entity -> domain model. */
    fun VendedorJpaEntity.toDomain(): Vendedor =
        Vendedor(
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
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun Vendedor.toEntity(existing: VendedorJpaEntity?): VendedorJpaEntity =
        VendedorJpaEntity(
            id = existing?.id ?: id,
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
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
