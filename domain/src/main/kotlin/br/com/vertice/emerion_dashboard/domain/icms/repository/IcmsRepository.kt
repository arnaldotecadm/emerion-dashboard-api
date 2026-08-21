package br.com.vertice.emerion_dashboard.domain.icms.repository

import br.com.vertice.emerion_dashboard.domain.icms.model.Icms

interface IcmsRepository {
    fun findByCnpjEmpresaAndCodigoIcms(cnpjEmpresa: String, codigoIcms: String): Icms?
    fun save(icms: Icms): Icms
}
