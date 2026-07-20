package br.com.vertice.emerion_dashboard.infrastructure.persistence.product.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.model.ProductJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductSpringDataRepository : JpaRepository<ProductJpaEntity, Long> {

    fun findByExternalId(externalId: String): ProductJpaEntity?
}
