package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.icms.Icms

/**
 * Outbound port used by icms ingestion to access persistence.
 */
interface IcmsRepositoryPort {
    fun findByCnpjEmpresaAndCodigoIcms(cnpjEmpresa: String, codigoIcms: String): Icms?

    fun save(icms: Icms): Icms
}
