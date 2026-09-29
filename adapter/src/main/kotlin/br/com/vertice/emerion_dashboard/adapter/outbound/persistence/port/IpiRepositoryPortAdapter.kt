package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.port

import br.com.vertice.emerion_dashboard.adapter.mapper.IpiPersistenceMapper.toDomain
import br.com.vertice.emerion_dashboard.adapter.mapper.IpiPersistenceMapper.toEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository.IpiRepository
import br.com.vertice.emerion_dashboard.application.outbound.port.IpiRepositoryPort
import br.com.vertice.emerion_dashboard.domain.ipi.Ipi
import org.springframework.stereotype.Component

@Component
class IpiRepositoryPortAdapter(
    private val ipiRepository: IpiRepository,
) : IpiRepositoryPort {

    override fun findByCnpjEmpresaAndCodigoIpi(cnpjEmpresa: String, codigoIpi: String): Ipi? =
        ipiRepository.findByCnpjEmpresaAndCodigoIpi(cnpjEmpresa, codigoIpi)?.toDomain()

    override fun save(ipi: Ipi): Ipi {
        val existing = ipiRepository.findByCnpjEmpresaAndCodigoIpi(ipi.cnpjEmpresa, ipi.codigoIpi)
        return ipiRepository.save(ipi.toEntity(existing)).toDomain()
    }
}
