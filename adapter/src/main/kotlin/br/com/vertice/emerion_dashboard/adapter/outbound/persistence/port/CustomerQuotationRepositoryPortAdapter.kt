package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.port

import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerQuotationPersistenceMapper.toDomain
import br.com.vertice.emerion_dashboard.adapter.mapper.CustomerQuotationPersistenceMapper.toEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository.CustomerQuotationRepository
import br.com.vertice.emerion_dashboard.application.outbound.port.CustomerQuotationRepositoryPort
import br.com.vertice.emerion_dashboard.domain.customerquotation.CustomerQuotation
import org.springframework.stereotype.Component

@Component
class CustomerQuotationRepositoryPortAdapter(
    private val customerQuotationRepository: CustomerQuotationRepository,
) : CustomerQuotationRepositoryPort {
    override fun findByCnpjEmpresaAndExternalId(cnpjEmpresa: String, externalId: String): CustomerQuotation? =
        customerQuotationRepository.findByCnpjEmpresaAndExternalId(cnpjEmpresa, externalId)?.toDomain()

    override fun save(customerQuotation: CustomerQuotation): CustomerQuotation {
        val existing = customerQuotationRepository.findByCnpjEmpresaAndExternalId(
            customerQuotation.cnpjEmpresa,
            customerQuotation.externalId,
        )
        return customerQuotationRepository.save(customerQuotation.toEntity(existing)).toDomain()
    }
}
