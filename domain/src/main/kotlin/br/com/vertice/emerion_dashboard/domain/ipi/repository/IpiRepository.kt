package br.com.vertice.emerion_dashboard.domain.ipi.repository

import br.com.vertice.emerion_dashboard.domain.ipi.model.Ipi

interface IpiRepository {
    fun findByCnpjEmpresaAndCodigoIpi(cnpjEmpresa: String, codigoIpi: String): Ipi?
    fun save(ipi: Ipi): Ipi
}
