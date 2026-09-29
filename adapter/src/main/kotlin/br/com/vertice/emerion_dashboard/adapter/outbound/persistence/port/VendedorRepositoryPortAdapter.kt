package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.port

import br.com.vertice.emerion_dashboard.adapter.mapper.VendedorPersistenceMapper.toDomain
import br.com.vertice.emerion_dashboard.adapter.mapper.VendedorPersistenceMapper.toEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository.VendedorRepository
import br.com.vertice.emerion_dashboard.application.outbound.port.VendedorRepositoryPort
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.domain.vendedor.Vendedor
import org.springframework.stereotype.Component
import org.springframework.data.domain.PageRequest as SpringPageRequest

@Component
class VendedorRepositoryPortAdapter(
    private val vendedorRepository: VendedorRepository,
) : VendedorRepositoryPort {

    override fun findById(id: Long): Vendedor? =
        vendedorRepository.findProjectionById(id)?.toDomain()

    override fun findByExternalId(externalId: String): Vendedor? =
        vendedorRepository.findByExternalId(externalId)?.toDomain()

    override fun findAll(
        pageRequest: PageRequest,
        nomeContains: String?,
        situacao: String?,
        cnpjEmpresa: String?,
    ): Page<Vendedor> {
        val springPageable = SpringPageRequest.of(pageRequest.page, pageRequest.size)
        val result = vendedorRepository.search(
            nomeContains?.takeIf { it.isNotBlank() },
            situacao?.takeIf { it.isNotBlank() },
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

    override fun save(vendedor: Vendedor): Vendedor {
        val existing = vendedor.id?.let { vendedorRepository.findById(it).orElse(null) }
            ?: vendedor.externalId.let { vendedorRepository.findByExternalId(it) }
        val entity = vendedor.toEntity(existing)
        val saved = vendedorRepository.save(entity)
        return saved.toDomain()
    }
}
