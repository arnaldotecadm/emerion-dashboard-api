package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection

import java.math.BigDecimal

interface LiberacaoDetalheProjection {
    val numeroSequenciaLiberacao: Int
    val classificacaoItem: String
    val codigoGrupo: String
    val codigoSubGrupo: String
    val codigoProduto: String
    val descricaoItemLiberacao: String
    val quantidadeNoPedido: BigDecimal
    val totalSeparado: BigDecimal
    val quantidadeRestante: BigDecimal
    val totalValorLiquido: BigDecimal
    val totalValorBruto: BigDecimal
    val percentualDesconto: BigDecimal
    val totalCusto: BigDecimal
    val percentualDeAcrescimo: BigDecimal
    val precoVendaItem: BigDecimal
    val precoPraticado: BigDecimal
    val custoPraticado: BigDecimal
}
