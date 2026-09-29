package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.port

import br.com.vertice.emerion_dashboard.adapter.mapper.ProductPersistenceMapper.toDomain
import br.com.vertice.emerion_dashboard.adapter.mapper.ProductPersistenceMapper.toEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository.ProductRepository
import br.com.vertice.emerion_dashboard.application.outbound.port.ProductRepositoryPort
import br.com.vertice.emerion_dashboard.domain.product.Product
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Component
import org.springframework.data.domain.PageRequest as SpringPageRequest

@Component
class ProductRepositoryPortAdapter(
    private val productRepository: ProductRepository,
) : ProductRepositoryPort {

    override fun findById(id: Long): Product? =
        productRepository.findProjectionById(id)?.toDomain()

    override fun findByExternalId(externalId: String): Product? =
        productRepository.findByExternalId(externalId)?.toDomain()

    override fun findAll(
        pageRequest: PageRequest,
        nomeContains: String?,
        cnpjEmpresa: String?,
    ): Page<Product> {
        val springPageable = SpringPageRequest.of(pageRequest.page, pageRequest.size)
        val result = productRepository.search(
            nomeContains?.takeIf { it.isNotBlank() },
            cnpjEmpresa?.takeIf { it.isNotBlank() },
            springPageable,
        )
        return Page(
            content = result.content.map { it.toDomain() },
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = result.totalElements,
        )
    }

    override fun save(product: Product): Product {
        val existing = product.id?.let { productRepository.findById(it).orElse(null) }
            ?: product.externalId.let { productRepository.findByExternalId(it) }
        val entity = product.toEntity(existing)
        val saved = productRepository.save(entity)
        return saved.toDomain()
    }
}
