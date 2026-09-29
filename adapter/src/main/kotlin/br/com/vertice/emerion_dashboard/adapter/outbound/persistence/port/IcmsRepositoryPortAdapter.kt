package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.port

import br.com.vertice.emerion_dashboard.adapter.mapper.IcmsPersistenceMapper.toDomain
import br.com.vertice.emerion_dashboard.adapter.mapper.IcmsPersistenceMapper.toEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository.IcmsRepository
import br.com.vertice.emerion_dashboard.application.outbound.port.IcmsRepositoryPort
import br.com.vertice.emerion_dashboard.domain.icms.Icms
import org.springframework.stereotype.Component

@Component
class IcmsRepositoryPortAdapter(
    private val icmsRepository: IcmsRepository,
) : IcmsRepositoryPort {

    override fun findByCnpjEmpresaAndCodigoIcms(cnpjEmpresa: String, codigoIcms: String): Icms? =
        icmsRepository.findByCnpjEmpresaAndCodigoIcms(cnpjEmpresa, codigoIcms)?.toDomain()

    override fun save(icms: Icms): Icms {
        val existing = icms.id?.let { icmsRepository.findById(it).orElse(null) }
            ?: icmsRepository.findByCnpjEmpresaAndCodigoIcms(icms.cnpjEmpresa, icms.codigoIcms)
        return icmsRepository.save(icms.toEntity(existing)).toDomain()
    }
}
