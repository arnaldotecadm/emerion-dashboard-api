package br.com.vertice.emerion_dashboard.infrastructure.persistence.fincre.adapter

import br.com.vertice.emerion_dashboard.domain.fincre.model.Fincre
import br.com.vertice.emerion_dashboard.domain.fincre.repository.FincreRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.fincre.mapper.FincrePersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.fincre.repository.FincreSpringDataRepository
import org.springframework.stereotype.Component

@Component
class FincreRepositoryAdapter(
    private val springDataRepository: FincreSpringDataRepository,
) : FincreRepository {
    override fun findByCnpjEmpresaAndDocumento(cnpjEmpresa: String, documento: String): Fincre? =
        springDataRepository.findByCnpjEmpresaAndDocumento(cnpjEmpresa, documento)?.let(FincrePersistenceMapper::toDomain)

    override fun save(fincre: Fincre): Fincre {
        val existing = springDataRepository.findByCnpjEmpresaAndDocumento(fincre.cnpjEmpresa, fincre.documento)
        val saved = springDataRepository.save(FincrePersistenceMapper.toEntity(fincre, existing))
        return FincrePersistenceMapper.toDomain(saved)
    }
}
