package br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.mapper

import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddress
import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddressDetail
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.model.CustomerAddressDetailJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.model.CustomerAddressJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.projection.CustomerAddressDetailProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.projection.CustomerAddressHeaderProjection

/** Maps between the domain model and the JPA entities/read projections. Kept out of the entity/domain classes on purpose. */
object CustomerAddressPersistenceMapper {

    /**
     * Read path: header + detail native-query projections (see
     * `CustomerAddressQueryRepository`) -> domain model.
     */
    fun toDomain(header: CustomerAddressHeaderProjection, details: List<CustomerAddressDetailProjection>): CustomerAddress =
        CustomerAddress(
            id = header.id,
            externalId = header.externalId,
            cnpjEmpresa = header.cnpjEmpresa,
            cpfCnpj = header.cpfCnpj,
            enderecos = details.map(::toDetailDomain),
            createdAt = header.createdAt,
            updatedAt = header.updatedAt,
        )

    private fun toDetailDomain(projection: CustomerAddressDetailProjection): CustomerAddressDetail =
        CustomerAddressDetail(
            tipo = projection.tipo,
            cep = projection.cep,
            endereco = projection.endereco,
            numero = projection.numero,
            referencia = projection.referencia,
            bairro = projection.bairro,
            cidade = projection.cidade,
            uf = projection.uf,
            telefone = projection.telefone,
            telefoneContato = projection.telefoneContato,
            complemento = projection.complemento,
            fax = projection.fax,
        )

    /** Write path: JPA entity -> domain model. */
    fun toDomain(entity: CustomerAddressJpaEntity): CustomerAddress =
        CustomerAddress(
            id = entity.id,
            externalId = entity.externalId,
            cnpjEmpresa = entity.cnpjEmpresa,
            cpfCnpj = entity.cpfCnpj,
            enderecos = entity.enderecos.map(::toDetailDomain),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    private fun toDetailDomain(entity: CustomerAddressDetailJpaEntity): CustomerAddressDetail =
        CustomerAddressDetail(
            tipo = entity.tipo,
            cep = entity.cep,
            endereco = entity.endereco,
            numero = entity.numero,
            referencia = entity.referencia,
            bairro = entity.bairro,
            cidade = entity.cidade,
            uf = entity.uf,
            telefone = entity.telefone,
            telefoneContato = entity.telefoneContato,
            complemento = entity.complemento,
            fax = entity.fax,
        )

    /**
     * Applies domain state onto a (possibly new) JPA entity, preserving the
     * generated id. When `existing` is provided, its managed `enderecos`
     * collection is cleared and repopulated in place so Hibernate's
     * orphanRemoval deletes the previous rows instead of leaving them
     * orphaned in the database.
     */
    fun toEntity(domain: CustomerAddress, existing: CustomerAddressJpaEntity?): CustomerAddressJpaEntity {
        val entity = existing ?: CustomerAddressJpaEntity(id = domain.id)
        entity.externalId = domain.externalId
        entity.cnpjEmpresa = domain.cnpjEmpresa
        entity.cpfCnpj = domain.cpfCnpj
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        entity.enderecos.clear()
        entity.enderecos.addAll(domain.enderecos.map { toDetailEntity(it, entity) })
        return entity
    }

    private fun toDetailEntity(detail: CustomerAddressDetail, parent: CustomerAddressJpaEntity): CustomerAddressDetailJpaEntity =
        CustomerAddressDetailJpaEntity(
            customerAddress = parent,
            tipo = detail.tipo,
            cep = detail.cep,
            endereco = detail.endereco,
            numero = detail.numero,
            referencia = detail.referencia,
            bairro = detail.bairro,
            cidade = detail.cidade,
            uf = detail.uf,
            telefone = detail.telefone,
            telefoneContato = detail.telefoneContato,
            complemento = detail.complemento,
            fax = detail.fax,
        )
}
