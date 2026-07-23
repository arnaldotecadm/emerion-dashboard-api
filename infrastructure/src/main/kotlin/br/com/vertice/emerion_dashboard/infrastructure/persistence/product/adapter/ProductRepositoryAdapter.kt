package br.com.vertice.emerion_dashboard.infrastructure.persistence.product.adapter

import br.com.vertice.emerion_dashboard.domain.product.model.Product
import br.com.vertice.emerion_dashboard.domain.product.repository.ProductRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.mapper.ProductPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.repository.ProductQueryRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.repository.ProductSpringDataRepository
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.stereotype.Component

/**
 * Adapter implementing the domain's outbound port (`ProductRepository`).
 * Reads (`findById`, `findAll`) go through `ProductQueryRepository`'s
 * native-query + projection path; writes/upserts (`save`, and the
 * `findByExternalId`/`findById` lookups needed to preserve the surrogate
 * key across an update) go through the JPA-entity-backed
 * `ProductSpringDataRepository`.
 */
@Component
class ProductRepositoryAdapter(
    private val springDataRepository: ProductSpringDataRepository,
    private val queryRepository: ProductQueryRepository,
) : ProductRepository {

    override fun findById(id: Long): Product? =
        queryRepository.findProjectionById(id)?.let(ProductPersistenceMapper::toDomain)

    override fun findByExternalId(externalId: String): Product? =
        springDataRepository.findByExternalId(externalId)?.let(ProductPersistenceMapper::toDomain)

    override fun findAll(
        pageRequest: PageRequest,
        nomeContains: String?,
        cnpjEmpresa: String?,
    ): Page<Product> {
        val springPageable = SpringPageRequest.of(pageRequest.page, pageRequest.size)
        val result = queryRepository.search(nomeContains?.takeIf { it.isNotBlank() }, cnpjEmpresa?.takeIf { it.isNotBlank() }, springPageable)
        return Page(
            content = result.content.map(ProductPersistenceMapper::toDomain),
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = result.totalElements,
        )
    }

    override fun save(product: Product): Product {
        val existing = product.id?.let { springDataRepository.findById(it).orElse(null) }
            ?: product.externalId.let { springDataRepository.findByExternalId(it) }
        val entity = ProductPersistenceMapper.toEntity(product, existing)
        val saved = springDataRepository.save(entity)
        return ProductPersistenceMapper.toDomain(saved)
    }
}

