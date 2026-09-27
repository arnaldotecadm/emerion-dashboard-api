package br.com.vertice.emerion_dashboard.infrastructure.persistence.projection

import java.math.BigDecimal

data class DashboardTotaisProjection(
    val quantidadePedidos: Int,
    val quantidadeClientes: Int,
    val quantidadeVendedores: Int,
    val totalFaturado: BigDecimal
)
