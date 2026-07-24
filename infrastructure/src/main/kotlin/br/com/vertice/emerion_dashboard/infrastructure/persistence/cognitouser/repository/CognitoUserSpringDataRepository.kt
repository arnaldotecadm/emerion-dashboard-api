package br.com.vertice.emerion_dashboard.infrastructure.persistence.cognitouser.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.cognitouser.model.CognitoUserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CognitoUserSpringDataRepository : JpaRepository<CognitoUserJpaEntity, Long> {
    fun findBySub(sub: String): CognitoUserJpaEntity?
}
