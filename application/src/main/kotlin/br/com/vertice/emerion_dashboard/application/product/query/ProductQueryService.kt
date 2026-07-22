package br.com.vertice.emerion_dashboard.application.product.query

import br.com.vertice.emerion_dashboard.application.product.query.model.ListProductsQuery
import br.com.vertice.emerion_dashboard.domain.product.exception.ProductNotFoundException
import br.com.vertice.emerion_dashboard.domain.product.model.Product
import br.com.vertice.emerion_dashboard.domain.product.repository.ProductRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductQueryService(
    private val productRepository: ProductRepository,
) : ProductQueryUseCase {

    @Transactional(readOnly = true)
    override fun getById(id: Long): Product =
        productRepository.findById(id) ?: throw ProductNotFoundException(id)

    @Transactional(readOnly = true)
    override fun list(query: ListProductsQuery): Page<Product> =
        productRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            nomeContains = query.nomeContains,
        )
}
