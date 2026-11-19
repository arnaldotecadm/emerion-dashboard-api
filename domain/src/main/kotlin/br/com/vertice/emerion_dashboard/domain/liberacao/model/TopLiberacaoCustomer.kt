package br.com.vertice.emerion_dashboard.domain.liberacao.model

import java.math.BigDecimal

data class TopLiberacaoCustomer(
    val codigoCliente: Long,
    val razaoSocial: String,
    val totalLiberadoComImpostos: BigDecimal,
)
