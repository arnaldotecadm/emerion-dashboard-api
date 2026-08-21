package br.com.vertice.emerion_dashboard.infrastructure.persistence.icms.adapter

import br.com.vertice.emerion_dashboard.domain.icms.model.Icms
import br.com.vertice.emerion_dashboard.domain.icms.repository.IcmsRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.icms.mapper.IcmsPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.icms.repository.IcmsSpringDataRepository
import org.springframework.stereotype.Component

@Component
class IcmsRepositoryAdapter(
    private val springDataRepository: IcmsSpringDataRepository,
) : IcmsRepository {
    override fun findByCnpjEmpresaAndCodigoIcms(cnpjEmpresa: String, codigoIcms: String): Icms? =
        springDataRepository.findByCnpjEmpresaAndCodigoIcms(cnpjEmpresa, codigoIcms)?.let(IcmsPersistenceMapper::toDomain)

    override fun save(icms: Icms): Icms {
        val existing = icms.id?.let { springDataRepository.findById(it).orElse(null) }
            ?: springDataRepository.findByCnpjEmpresaAndCodigoIcms(icms.cnpjEmpresa, icms.codigoIcms)
        return IcmsPersistenceMapper.toDomain(springDataRepository.save(IcmsPersistenceMapper.toEntity(icms, existing)))
    }
}
