package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection

import java.math.BigDecimal

interface TopSoldItemProjection {
    val codigoGrupo: String
    val codigoSubGrupo: String
    val codigoProduto: String
    val descricaoItemLiberacao: String
    val totalSeparado: BigDecimal
}
