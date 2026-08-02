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
            descricaoReduzida = projection.descricaoReduzida,
            referenciaInterna = projection.referenciaInterna,
            ncm = projection.ncm,
            cest = projection.cest,
            origemProduto = projection.origemProduto,
            categoria = projection.categoria,
            tipo = projection.tipo,
            marca = projection.marca,
            unidade = projection.unidade,
            pesoLiquido = projection.pesoLiquido,
            pesoBruto = projection.pesoBruto,
            descontinuado = projection.descontinuado,
            codigoBarras = projection.codigoBarras,
            codigoBarrasProprio = projection.codigoBarrasProprio,
            preco = projection.preco,
            preco2 = projection.preco2,
            preco3 = projection.preco3,
            preco4 = projection.preco4,
            preco5 = projection.preco5,
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
            descricaoReduzida = entity.descricaoReduzida,
            referenciaInterna = entity.referenciaInterna,
            ncm = entity.ncm,
            cest = entity.cest,
            origemProduto = entity.origemProduto,
            categoria = entity.categoria,
            tipo = entity.tipo,
            marca = entity.marca,
            unidade = entity.unidade,
            pesoLiquido = entity.pesoLiquido,
            pesoBruto = entity.pesoBruto,
            descontinuado = entity.descontinuado,
            codigoBarras = entity.codigoBarras,
            codigoBarrasProprio = entity.codigoBarrasProprio,
            preco = entity.preco,
            preco2 = entity.preco2,
            preco3 = entity.preco3,
            preco4 = entity.preco4,
            preco5 = entity.preco5,
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
            descricaoReduzida = domain.descricaoReduzida,
            referenciaInterna = domain.referenciaInterna,
            ncm = domain.ncm,
            cest = domain.cest,
            origemProduto = domain.origemProduto,
            categoria = domain.categoria,
            tipo = domain.tipo,
            marca = domain.marca,
            unidade = domain.unidade,
            pesoLiquido = domain.pesoLiquido,
            pesoBruto = domain.pesoBruto,
            descontinuado = domain.descontinuado,
            codigoBarras = domain.codigoBarras,
            codigoBarrasProprio = domain.codigoBarrasProprio,
            preco = domain.preco,
            preco2 = domain.preco2,
            preco3 = domain.preco3,
            preco4 = domain.preco4,
            preco5 = domain.preco5,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )
}
