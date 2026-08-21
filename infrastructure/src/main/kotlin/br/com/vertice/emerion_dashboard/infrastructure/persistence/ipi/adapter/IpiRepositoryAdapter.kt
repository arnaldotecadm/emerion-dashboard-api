package br.com.vertice.emerion_dashboard.infrastructure.persistence.ipi.adapter

import br.com.vertice.emerion_dashboard.domain.ipi.model.Ipi
import br.com.vertice.emerion_dashboard.domain.ipi.repository.IpiRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.ipi.mapper.IpiPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.ipi.repository.IpiSpringDataRepository
import org.springframework.stereotype.Component

@Component
class IpiRepositoryAdapter(
    private val springDataRepository: IpiSpringDataRepository,
) : IpiRepository {
    override fun findByCnpjEmpresaAndCodigoIpi(cnpjEmpresa: String, codigoIpi: String): Ipi? =
        springDataRepository.findByCnpjEmpresaAndCodigoIpi(cnpjEmpresa, codigoIpi)?.let(IpiPersistenceMapper::toDomain)

    override fun save(ipi: Ipi): Ipi {
        val existing = springDataRepository.findByCnpjEmpresaAndCodigoIpi(ipi.cnpjEmpresa, ipi.codigoIpi)
        return IpiPersistenceMapper.toDomain(springDataRepository.save(IpiPersistenceMapper.toEntity(ipi, existing)))
    }
}
