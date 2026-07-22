package br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.vendedor.model.VendedorJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface VendedorSpringDataRepository : JpaRepository<VendedorJpaEntity, Long> {

    fun findByExternalId(externalId: String): VendedorJpaEntity?
}
