package br.com.vertice.emerion_dashboard.domain.apikey.model

import java.time.Instant

data class ApiKey(
    val id: Long,
    val keyValue: String,
    val serverName: String,
    val enabled: Boolean,
    val description: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val lastUsedAt: Instant? = null
)
