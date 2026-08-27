package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.adapter

import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.repository.LiberacaoRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.mapper.LiberacaoPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.repository.LiberacaoQueryRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.repository.LiberacaoSpringDataRepository
import org.springframework.data.domain.PageRequest as SpringPageRequest
import org.springframework.stereotype.Component

@Component
class LiberacaoRepositoryAdapter(
    private val springDataRepository: LiberacaoSpringDataRepository,
    private val queryRepository: LiberacaoQueryRepository,
) : LiberacaoRepository {
    override fun findById(id: Long): Liberacao? =
        queryRepository.findProjectionById(id)?.let(::toDomain)

    override fun findByNumeroPedidoAndNumeroLiberacao(numeroPedido: String, numeroLiberacao: Int): Liberacao? =
        springDataRepository.findByNumeroPedidoAndNumeroLiberacao(numeroPedido, numeroLiberacao)
            ?.let(LiberacaoPersistenceMapper::toDomain)

    override fun findAll(pageRequest: PageRequest, numeroPedido: String?): Page<Liberacao> {
        val result = queryRepository.search(
            numeroPedido?.takeIf { it.isNotBlank() },
            SpringPageRequest.of(pageRequest.page, pageRequest.size),
        )
        return Page(
            content = result.content.map(::toDomain),
            page = pageRequest.page,
            size = pageRequest.size,
            totalElements = result.totalElements,
        )
    }

    override fun save(liberacao: Liberacao): Liberacao {
        val existing = springDataRepository.findByNumeroPedidoAndNumeroLiberacao(
            liberacao.numeroPedido,
            liberacao.numeroLiberacao,
        )
        return springDataRepository.save(LiberacaoPersistenceMapper.toEntity(liberacao, existing))
            .let(LiberacaoPersistenceMapper::toDomain)
    }

    private fun toDomain(projection: LiberacaoProjection): Liberacao =
        LiberacaoPersistenceMapper.toDomain(
            projection,
            queryRepository.findDetalhes(projection.numeroPedido, projection.numeroLiberacao),
        )
}
