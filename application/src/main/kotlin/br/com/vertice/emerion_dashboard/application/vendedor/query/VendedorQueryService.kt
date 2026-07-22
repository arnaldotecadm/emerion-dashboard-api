package br.com.vertice.emerion_dashboard.application.vendedor.query

import br.com.vertice.emerion_dashboard.application.vendedor.query.model.ListVendedoresQuery
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.domain.vendedor.exception.VendedorNotFoundException
import br.com.vertice.emerion_dashboard.domain.vendedor.model.Vendedor
import br.com.vertice.emerion_dashboard.domain.vendedor.repository.VendedorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VendedorQueryService(
    private val vendedorRepository: VendedorRepository,
) : VendedorQueryUseCase {

    @Transactional(readOnly = true)
    override fun getById(id: Long): Vendedor =
        vendedorRepository.findById(id) ?: throw VendedorNotFoundException(id)

    @Transactional(readOnly = true)
    override fun list(query: ListVendedoresQuery): Page<Vendedor> =
        vendedorRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            nomeContains = query.nomeContains,
            situacao = query.situacao,
        )
}
