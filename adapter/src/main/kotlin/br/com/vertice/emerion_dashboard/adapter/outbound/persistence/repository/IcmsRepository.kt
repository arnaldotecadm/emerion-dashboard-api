package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.IcmsJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface IcmsRepository : JpaRepository<IcmsJpaEntity, Long> {
    fun findByCnpjEmpresaAndCodigoIcms(cnpjEmpresa: String, codigoIcms: String): IcmsJpaEntity?
}
