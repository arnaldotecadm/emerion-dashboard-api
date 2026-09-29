package br.com.vertice.emerion_dashboard.application.product

import br.com.vertice.emerion_dashboard.application.outbound.port.ProductRepositoryPort
import br.com.vertice.emerion_dashboard.domain.product.ListProductsQuery
import br.com.vertice.emerion_dashboard.domain.product.Product
import br.com.vertice.emerion_dashboard.domain.product.exception.ProductNotFoundException
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepositoryPort,
) {

    @Transactional(readOnly = true)
    fun getById(id: Long): Product =
        productRepository.findById(id) ?: throw ProductNotFoundException(id)

    @Transactional(readOnly = true)
    fun getByExternalId(externalId: String): Product =
        productRepository.findByExternalId(externalId) ?: throw ProductNotFoundException(externalId)

    @Transactional(readOnly = true)
    fun list(query: ListProductsQuery): Page<Product> =
        productRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            nomeContains = query.nomeContains,
            cnpjEmpresa = query.cnpjEmpresa,
        )
}
