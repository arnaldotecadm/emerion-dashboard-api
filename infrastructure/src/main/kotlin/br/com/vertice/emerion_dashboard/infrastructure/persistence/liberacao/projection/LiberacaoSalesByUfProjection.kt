package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection

import java.math.BigDecimal

interface LiberacaoSalesByUfProjection {
    val uf: String
    val amount: BigDecimal
    val representationPercentage: BigDecimal
}
