package br.com.vertice.emerion_dashboard.domain.fincre.repository

import br.com.vertice.emerion_dashboard.domain.fincre.model.Fincre

interface FincreRepository {
    fun findByCnpjEmpresaAndDocumento(cnpjEmpresa: String, documento: String): Fincre?
    fun save(fincre: Fincre): Fincre
}
