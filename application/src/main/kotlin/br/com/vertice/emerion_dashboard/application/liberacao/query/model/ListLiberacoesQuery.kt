package br.com.vertice.emerion_dashboard.application.liberacao.query.model

data class ListLiberacoesQuery(
    val page: Int,
    val size: Int,
    val numeroPedido: String? = null,
)
