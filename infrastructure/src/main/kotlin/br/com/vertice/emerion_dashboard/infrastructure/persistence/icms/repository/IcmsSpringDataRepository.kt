package br.com.vertice.emerion_dashboard.infrastructure.persistence.icms.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.icms.model.IcmsJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface IcmsSpringDataRepository : JpaRepository<IcmsJpaEntity, Long> {
    fun findByCnpjEmpresaAndCodigoIcms(cnpjEmpresa: String, codigoIcms: String): IcmsJpaEntity?
}
