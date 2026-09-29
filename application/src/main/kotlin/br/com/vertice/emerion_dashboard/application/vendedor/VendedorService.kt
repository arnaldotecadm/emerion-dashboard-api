package br.com.vertice.emerion_dashboard.application.vendedor

import br.com.vertice.emerion_dashboard.application.outbound.port.VendedorRepositoryPort
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.domain.vendedor.ListVendedoresQuery
import br.com.vertice.emerion_dashboard.domain.vendedor.Vendedor
import br.com.vertice.emerion_dashboard.domain.vendedor.exception.VendedorNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VendedorService(
    private val vendedorRepository: VendedorRepositoryPort,
) {

    @Transactional(readOnly = true)
    fun getById(id: Long): Vendedor =
        vendedorRepository.findById(id) ?: throw VendedorNotFoundException(id)

    @Transactional(readOnly = true)
    fun getByExternalId(externalId: String): Vendedor =
        vendedorRepository.findByExternalId(externalId) ?: throw VendedorNotFoundException(externalId)

    @Transactional(readOnly = true)
    fun list(query: ListVendedoresQuery): Page<Vendedor> =
        vendedorRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            nomeContains = query.nomeContains,
            situacao = query.situacao,
            cnpjEmpresa = query.cnpjEmpresa,
        )
}
