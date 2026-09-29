package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.port

import br.com.vertice.emerion_dashboard.adapter.mapper.FincrePersistenceMapper.toDomain
import br.com.vertice.emerion_dashboard.adapter.mapper.FincrePersistenceMapper.toEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository.FincreRepository
import br.com.vertice.emerion_dashboard.application.outbound.port.FincreRepositoryPort
import br.com.vertice.emerion_dashboard.domain.fincre.Fincre
import org.springframework.stereotype.Component

@Component
class FincreRepositoryPortAdapter(
    private val fincreRepository: FincreRepository,
) : FincreRepositoryPort {

    override fun findByCnpjEmpresaAndDocumento(cnpjEmpresa: String, documento: String): Fincre? =
        fincreRepository.findByCnpjEmpresaAndDocumento(cnpjEmpresa, documento)?.toDomain()

    override fun save(fincre: Fincre): Fincre {
        val existing = fincreRepository.findByCnpjEmpresaAndDocumento(fincre.cnpjEmpresa, fincre.documento)
        val saved = fincreRepository.save(fincre.toEntity(existing))
        return saved.toDomain()
    }
}
