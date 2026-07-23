package br.com.vertice.emerion_dashboard.infrastructure.persistence.product.mapper

import br.com.vertice.emerion_dashboard.domain.product.model.Product
import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.model.ProductJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.projection.ProductProjection

/** Maps between the domain model and the JPA entity/read projection. Kept out of the entity/domain classes on purpose. */
object ProductPersistenceMapper {

    /** Read path: native-query projection (see `ProductQueryRepository`) -> domain model. */
    fun toDomain(projection: ProductProjection): Product =
        Product(
            id = projection.id,
            externalId = projection.externalId,
            cnpjEmpresa = projection.cnpjEmpresa,
            nome = projection.nome,
            preco = projection.preco,
            createdAt = projection.createdAt,
            updatedAt = projection.updatedAt,
        )

    /** Write path: JPA entity -> domain model. */
    fun toDomain(entity: ProductJpaEntity): Product =
        Product(
            id = entity.id,
            externalId = entity.externalId,
            cnpjEmpresa = entity.cnpjEmpresa,
            nome = entity.nome,
            preco = entity.preco,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun toEntity(domain: Product, existing: ProductJpaEntity?): ProductJpaEntity =
        ProductJpaEntity(
            id = existing?.id ?: domain.id,
            externalId = domain.externalId,
            cnpjEmpresa = domain.cnpjEmpresa,
            nome = domain.nome,
            preco = domain.preco,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )
}
