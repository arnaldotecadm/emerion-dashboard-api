package br.com.vertice.emerion_dashboard.infrastructure.persistence.fincre.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.fincre.model.FincreTituloReceberJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FincreSpringDataRepository : JpaRepository<FincreTituloReceberJpaEntity, Long> {
    fun findByCnpjEmpresaAndDocumento(cnpjEmpresa: String, documento: String): FincreTituloReceberJpaEntity?
}
