package br.com.vertice.emerion_dashboard.domain.liberacao.model

import java.math.BigDecimal

data class TopSoldItem(
    val codigoGrupo: String,
    val codigoSubGrupo: String,
    val codigoProduto: String,
    val descricaoItemLiberacao: String,
    val totalSeparado: BigDecimal,
)
