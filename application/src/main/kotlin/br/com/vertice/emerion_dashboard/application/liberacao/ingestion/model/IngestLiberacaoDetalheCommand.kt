package br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model

import java.math.BigDecimal

data class IngestLiberacaoDetalheCommand(
    val numeroSequenciaLiberacao: Int,
    val classificacaoItem: String,
    val codigoGrupo: String,
    val codigoSubGrupo: String,
    val codigoProduto: String,
    val descricaoItemLiberacao: String,
    val quantidadeNoPedido: BigDecimal,
    val totalSeparado: BigDecimal,
    val quantidadeRestante: BigDecimal,
    val totalValorLiquido: BigDecimal,
    val totalValorBruto: BigDecimal,
    val percentualDesconto: BigDecimal,
    val totalCusto: BigDecimal,
    val percentualDeAcrescimo: BigDecimal,
    val precoVendaItem: BigDecimal,
    val precoPraticado: BigDecimal,
    val custoPraticado: BigDecimal,
)
