package br.com.vertice.emerion_dashboard.domain.dashboard

import java.math.BigDecimal

data class DashboardTotais(
    val quantidadePedidos: Int,
    val quantidadeClientes: Int,
    val quantidadeVendedores: Int,
    val totalFaturado: BigDecimal
)
