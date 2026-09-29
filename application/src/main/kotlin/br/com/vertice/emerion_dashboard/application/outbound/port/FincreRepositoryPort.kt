package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.fincre.Fincre

/**
 * Outbound port used by fincre ingestion to access persistence.
 */
interface FincreRepositoryPort {
    fun findByCnpjEmpresaAndDocumento(cnpjEmpresa: String, documento: String): Fincre?

    fun save(fincre: Fincre): Fincre
}
