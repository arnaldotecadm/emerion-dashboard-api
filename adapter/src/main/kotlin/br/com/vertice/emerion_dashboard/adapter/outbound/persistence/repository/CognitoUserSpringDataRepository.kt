package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.CognitoUserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CognitoUserSpringDataRepository : JpaRepository<CognitoUserJpaEntity, Long> {
    fun findBySub(sub: String): CognitoUserJpaEntity?
}
