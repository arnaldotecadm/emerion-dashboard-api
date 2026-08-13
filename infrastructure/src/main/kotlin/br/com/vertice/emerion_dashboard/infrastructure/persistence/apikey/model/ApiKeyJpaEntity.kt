package br.com.vertice.emerion_dashboard.infrastructure.persistence.apikey.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "api_key")
data class ApiKeyJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    
    @Column(nullable = false, unique = true)
    var keyValue: String = "",
    
    @Column(nullable = false)
    var serverName: String = "",
    
    @Column(nullable = false)
    var enabled: Boolean = true,
    
    var description: String? = null,
    
    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),
    
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),
    
    var lastUsedAt: Instant? = null
)
