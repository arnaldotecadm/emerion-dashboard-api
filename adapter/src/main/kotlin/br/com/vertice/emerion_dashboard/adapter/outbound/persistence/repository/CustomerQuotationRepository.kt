package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.CustomerQuotationJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerQuotationRepository : JpaRepository<CustomerQuotationJpaEntity, Long> {
    fun findByCnpjEmpresaAndExternalId(cnpjEmpresa: String, externalId: String): CustomerQuotationJpaEntity?
}
