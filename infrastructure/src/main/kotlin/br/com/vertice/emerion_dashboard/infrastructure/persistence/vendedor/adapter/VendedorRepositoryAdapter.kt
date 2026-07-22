package br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.adapter

import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.domain.vendedor.model.Vendedor
import br.com.vertice.emerion_dashboard.domain.vendedor.repository.VendedorRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.mapper.VendedorPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.repository.VendedorQueryRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.repository.VendedorSpringDataRepository
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.stereotype.Component

/**
 * Adapter implementing the domain's outbound port (`VendedorRepository`).
 * Reads (`findById`, `findAll`) go through `VendedorQueryRepository`'s
 * native-query + projection path; writes/upserts (`save`, and the
 * `findByExternalId`/`findById` lookups needed to preserve the surrogate
 * key across an update) go through the JPA-entity-backed
 * `VendedorSpringDataRepository`.
 */
@Component
class VendedorRepositoryAdapter(
    private val springDataRepository: VendedorSpringDataRepository,
    private val queryRepository: VendedorQueryRepository,
) : VendedorRepository {

    override fun findById(id: Long): Vendedor? =
        queryRepository.findProjectionById(id)?.let(VendedorPersistenceMapper::toDomain)

    override fun findByExternalId(externalId: String): Vendedor? =
        springDataRepository.findByExternalId(externalId)?.let(VendedorPersistenceMapper::toDomain)

    override fun findAll(
        pageRequest: PageRequest,
        nomeContains: String?,
        situacao: String?,
    ): Page<Vendedor> {
        val springPageable = SpringPageRequest.of(pageRequest.page, pageRequest.size)
        val result = queryRepository.search(
            nomeContains?.takeIf { it.isNotBlank() },
            situacao?.takeIf { it.isNotBlank() },
            springPageable,
        )
        return Page(
            content = result.content.map(VendedorPersistenceMapper::toDomain),
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = result.totalElements,
        )
    }

    override fun save(vendedor: Vendedor): Vendedor {
        val existing = vendedor.id?.let { springDataRepository.findById(it).orElse(null) }
            ?: vendedor.externalId.let { springDataRepository.findByExternalId(it) }
        val entity = VendedorPersistenceMapper.toEntity(vendedor, existing)
        val saved = springDataRepository.save(entity)
        return VendedorPersistenceMapper.toDomain(saved)
    }
}
