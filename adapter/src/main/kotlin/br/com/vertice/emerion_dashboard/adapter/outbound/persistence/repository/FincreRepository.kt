package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.FincreTituloReceberJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FincreRepository : JpaRepository<FincreTituloReceberJpaEntity, Long> {
    fun findByCnpjEmpresaAndDocumento(cnpjEmpresa: String, documento: String): FincreTituloReceberJpaEntity?
}
