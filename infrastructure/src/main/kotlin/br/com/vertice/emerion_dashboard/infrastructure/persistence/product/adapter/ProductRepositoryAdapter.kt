package br.com.vertice.emerion_dashboard.infrastructure.persistence.product.adapter

import br.com.vertice.emerion_dashboard.domain.product.model.Product
import br.com.vertice.emerion_dashboard.domain.product.repository.ProductRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.mapper.ProductPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.repository.ProductSpringDataRepository
import org.springframework.stereotype.Component

/**
 * Adapter implementing the domain's outbound port (`ProductRepository`) on
 * top of Spring Data JPA. This is the only class allowed to depend on both
 * the domain model and the JPA entity.
 */
@Component
class ProductRepositoryAdapter(
    private val springDataRepository: ProductSpringDataRepository,
) : ProductRepository {

    override fun findByExternalId(externalId: String): Product? =
        springDataRepository.findByExternalId(externalId)?.let(ProductPersistenceMapper::toDomain)

    override fun save(product: Product): Product {
        val existing = product.id?.let { springDataRepository.findById(it).orElse(null) }
            ?: product.externalId.let { springDataRepository.findByExternalId(it) }
        val entity = ProductPersistenceMapper.toEntity(product, existing)
        val saved = springDataRepository.save(entity)
        return ProductPersistenceMapper.toDomain(saved)
    }
}
