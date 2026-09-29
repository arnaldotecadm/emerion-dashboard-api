package br.com.vertice.emerion_dashboard.application.outbound.port

import br.com.vertice.emerion_dashboard.domain.ipi.Ipi

/**
 * Outbound port used by ipi ingestion to access persistence.
 */
interface IpiRepositoryPort {
    fun findByCnpjEmpresaAndCodigoIpi(cnpjEmpresa: String, codigoIpi: String): Ipi?

    fun save(ipi: Ipi): Ipi
}
