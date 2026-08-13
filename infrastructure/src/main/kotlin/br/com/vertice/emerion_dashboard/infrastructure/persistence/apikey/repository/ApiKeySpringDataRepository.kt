package br.com.vertice.emerion_dashboard.infrastructure.persistence.apikey.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.apikey.model.ApiKeyJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ApiKeySpringDataRepository : JpaRepository<ApiKeyJpaEntity, Long> {
    fun findByKeyValueAndEnabledTrue(keyValue: String): ApiKeyJpaEntity?
    
    @Modifying
    @Query("UPDATE ApiKeyJpaEntity a SET a.lastUsedAt = :timestamp WHERE a.id = :id")
    fun updateLastUsedAt(@Param("id") id: Long, @Param("timestamp") timestamp: Instant)
}
