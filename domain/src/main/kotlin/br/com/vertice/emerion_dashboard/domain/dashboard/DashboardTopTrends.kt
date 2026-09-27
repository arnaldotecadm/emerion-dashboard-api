package br.com.vertice.emerion_dashboard.domain.dashboard

import java.math.BigDecimal

data class DashboardTopTrends(
    val topCustomers: List<TopTrends>,
    val topVendedores: List<TopTrends>,
    val quantidadePedidos: Int,
    val quantidadeClientes: Int,
    val quantidadeVendedores: Int,
    val totalFaturado: BigDecimal,
    val ticketMedio: BigDecimal
)
