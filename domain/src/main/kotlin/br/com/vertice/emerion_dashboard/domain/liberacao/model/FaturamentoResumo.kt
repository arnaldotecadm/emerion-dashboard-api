package br.com.vertice.emerion_dashboard.domain.liberacao.model

data class FaturamentoResumo(
    val topSoldItems: List<TopSoldItem>,
    val topCustomers: List<TopLiberacaoCustomer>,
    val salesByUf: List<LiberacaoSalesByUf>,
    val salespeople: List<LiberacaoSalespersonSummary>,
    val salespersonTotals: List<LiberacaoSalespersonTotals>,
)
