package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.IpiJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface IpiRepository : JpaRepository<IpiJpaEntity, Long> {
    fun findByCnpjEmpresaAndCodigoIpi(cnpjEmpresa: String, codigoIpi: String): IpiJpaEntity?
}
