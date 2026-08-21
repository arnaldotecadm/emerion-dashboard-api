package br.com.vertice.emerion_dashboard.infrastructure.persistence.ipi.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.ipi.model.IpiJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface IpiSpringDataRepository : JpaRepository<IpiJpaEntity, Long> {
    fun findByCnpjEmpresaAndCodigoIpi(cnpjEmpresa: String, codigoIpi: String): IpiJpaEntity?
}
