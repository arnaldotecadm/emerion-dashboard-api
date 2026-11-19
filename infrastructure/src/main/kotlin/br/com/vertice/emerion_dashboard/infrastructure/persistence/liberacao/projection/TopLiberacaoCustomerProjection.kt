package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection

import java.math.BigDecimal

interface TopLiberacaoCustomerProjection {
    val codigoCliente: Long
    val razaoSocial: String
    val totalLiberadoComImpostos: BigDecimal
}
