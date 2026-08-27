package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.model.LiberacaoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LiberacaoSpringDataRepository : JpaRepository<LiberacaoJpaEntity, Long> {
    fun findByNumeroPedidoAndNumeroLiberacao(numeroPedido: String, numeroLiberacao: Int): LiberacaoJpaEntity?
}
