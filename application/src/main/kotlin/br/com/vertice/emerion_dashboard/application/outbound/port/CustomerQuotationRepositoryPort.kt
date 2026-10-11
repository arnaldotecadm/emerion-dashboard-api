package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.customerquotation.CustomerQuotation

interface CustomerQuotationRepositoryPort {
    fun findByCnpjEmpresaAndExternalId(cnpjEmpresa: String, externalId: String): CustomerQuotation?

    fun save(customerQuotation: CustomerQuotation): CustomerQuotation
}
