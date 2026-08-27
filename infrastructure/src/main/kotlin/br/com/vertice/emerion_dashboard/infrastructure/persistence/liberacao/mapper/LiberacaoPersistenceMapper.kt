package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.mapper

import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.model.LiberacaoDetalhe
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.model.LiberacaoDetalheJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.model.LiberacaoJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoDetalheProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection.LiberacaoProjection

object LiberacaoPersistenceMapper {
    fun toDomain(entity: LiberacaoJpaEntity): Liberacao =
        Liberacao(
            id = entity.id,
            cnpjEmpresa = entity.cnpjEmpresa,
            codigoEmpresa = entity.codigoEmpresa,
            dataPedido = entity.dataPedido,
            numeroPedido = entity.numeroPedido,
            numeroLiberacao = entity.numeroLiberacao,
            dataLiberacao = entity.dataLiberacao,
            horaLiberacao = entity.horaLiberacao,
            codigoCliente = entity.codigoCliente,
            quantidadeSeparada = entity.quantidadeSeparada,
            totalLiberadoSemImpostos = entity.totalLiberadoSemImpostos,
            totalLiberadoComImpostos = entity.totalLiberadoComImpostos,
            situacaoLiberacao = entity.situacaoLiberacao,
            codigoVendedor = entity.codigoVendedor,
            comissaoLiberacao = entity.comissaoLiberacao,
            totalCusto = entity.totalCusto,
            detalhes = entity.detalhes.map(::toDetalheDomain),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toDomain(
        projection: LiberacaoProjection,
        detalhes: List<LiberacaoDetalheProjection>,
    ): Liberacao =
        Liberacao(
            id = projection.id,
            cnpjEmpresa = projection.cnpjEmpresa,
            codigoEmpresa = projection.codigoEmpresa,
            dataPedido = projection.dataPedido,
            numeroPedido = projection.numeroPedido,
            numeroLiberacao = projection.numeroLiberacao,
            dataLiberacao = projection.dataLiberacao,
            horaLiberacao = projection.horaLiberacao,
            codigoCliente = projection.codigoCliente,
            quantidadeSeparada = projection.quantidadeSeparada,
            totalLiberadoSemImpostos = projection.totalLiberadoSemImpostos,
            totalLiberadoComImpostos = projection.totalLiberadoComImpostos,
            situacaoLiberacao = projection.situacaoLiberacao,
            codigoVendedor = projection.codigoVendedor,
            comissaoLiberacao = projection.comissaoLiberacao,
            totalCusto = projection.totalCusto,
            detalhes = detalhes.map(::toDetalheDomain),
            createdAt = projection.createdAt,
            updatedAt = projection.updatedAt,
        )

    private fun toDetalheDomain(entity: LiberacaoDetalheJpaEntity) =
        LiberacaoDetalhe(
            numeroSequenciaLiberacao = entity.numeroSequenciaLiberacao,
            classificacaoItem = entity.classificacaoItem,
            codigoGrupo = entity.codigoGrupo,
            codigoSubGrupo = entity.codigoSubGrupo,
            codigoProduto = entity.codigoProduto,
            descricaoItemLiberacao = entity.descricaoItemLiberacao,
            quantidadeNoPedido = entity.quantidadeNoPedido,
            totalSeparado = entity.totalSeparado,
            quantidadeRestante = entity.quantidadeRestante,
            totalValorLiquido = entity.totalValorLiquido,
            totalValorBruto = entity.totalValorBruto,
            percentualDesconto = entity.percentualDesconto,
            totalCusto = entity.totalCusto,
            percentualDeAcrescimo = entity.percentualDeAcrescimo,
            precoVendaItem = entity.precoVendaItem,
            precoPraticado = entity.precoPraticado,
            custoPraticado = entity.custoPraticado,
        )

    private fun toDetalheDomain(projection: LiberacaoDetalheProjection) =
        LiberacaoDetalhe(
            numeroSequenciaLiberacao = projection.numeroSequenciaLiberacao,
            classificacaoItem = projection.classificacaoItem,
            codigoGrupo = projection.codigoGrupo,
            codigoSubGrupo = projection.codigoSubGrupo,
            codigoProduto = projection.codigoProduto,
            descricaoItemLiberacao = projection.descricaoItemLiberacao,
            quantidadeNoPedido = projection.quantidadeNoPedido,
            totalSeparado = projection.totalSeparado,
            quantidadeRestante = projection.quantidadeRestante,
            totalValorLiquido = projection.totalValorLiquido,
            totalValorBruto = projection.totalValorBruto,
            percentualDesconto = projection.percentualDesconto,
            totalCusto = projection.totalCusto,
            percentualDeAcrescimo = projection.percentualDeAcrescimo,
            precoVendaItem = projection.precoVendaItem,
            precoPraticado = projection.precoPraticado,
            custoPraticado = projection.custoPraticado,
        )

    fun toEntity(domain: Liberacao, existing: LiberacaoJpaEntity?): LiberacaoJpaEntity {
        val entity = existing ?: LiberacaoJpaEntity(id = domain.id)
        entity.cnpjEmpresa = domain.cnpjEmpresa
        entity.codigoEmpresa = domain.codigoEmpresa
        entity.dataPedido = domain.dataPedido
        entity.numeroPedido = domain.numeroPedido
        entity.numeroLiberacao = domain.numeroLiberacao
        entity.dataLiberacao = domain.dataLiberacao
        entity.horaLiberacao = domain.horaLiberacao
        entity.codigoCliente = domain.codigoCliente
        entity.quantidadeSeparada = domain.quantidadeSeparada
        entity.totalLiberadoSemImpostos = domain.totalLiberadoSemImpostos
        entity.totalLiberadoComImpostos = domain.totalLiberadoComImpostos
        entity.situacaoLiberacao = domain.situacaoLiberacao
        entity.codigoVendedor = domain.codigoVendedor
        entity.comissaoLiberacao = domain.comissaoLiberacao
        entity.totalCusto = domain.totalCusto
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        upsertDetalhes(entity, domain.detalhes)
        return entity
    }

    private fun upsertDetalhes(entity: LiberacaoJpaEntity, detalhes: List<LiberacaoDetalhe>) {
        val existingBySequencia = entity.detalhes.associateBy { it.numeroSequenciaLiberacao }
        val incomingSequencias = detalhes.map { it.numeroSequenciaLiberacao }.toSet()
        entity.detalhes.removeAll { it.numeroSequenciaLiberacao !in incomingSequencias }
        detalhes.forEach { detalhe ->
            existingBySequencia[detalhe.numeroSequenciaLiberacao]?.let {
                applyDetalheFields(it, detalhe)
            } ?: entity.detalhes.add(toDetalheEntity(detalhe, entity))
        }
    }

    private fun applyDetalheFields(entity: LiberacaoDetalheJpaEntity, detalhe: LiberacaoDetalhe) {
        entity.classificacaoItem = detalhe.classificacaoItem
        entity.codigoGrupo = detalhe.codigoGrupo
        entity.codigoSubGrupo = detalhe.codigoSubGrupo
        entity.codigoProduto = detalhe.codigoProduto
        entity.descricaoItemLiberacao = detalhe.descricaoItemLiberacao
        entity.quantidadeNoPedido = detalhe.quantidadeNoPedido
        entity.totalSeparado = detalhe.totalSeparado
        entity.quantidadeRestante = detalhe.quantidadeRestante
        entity.totalValorLiquido = detalhe.totalValorLiquido
        entity.totalValorBruto = detalhe.totalValorBruto
        entity.percentualDesconto = detalhe.percentualDesconto
        entity.totalCusto = detalhe.totalCusto
        entity.percentualDeAcrescimo = detalhe.percentualDeAcrescimo
        entity.precoVendaItem = detalhe.precoVendaItem
        entity.precoPraticado = detalhe.precoPraticado
        entity.custoPraticado = detalhe.custoPraticado
    }

    private fun toDetalheEntity(
        detalhe: LiberacaoDetalhe,
        parent: LiberacaoJpaEntity,
    ) = LiberacaoDetalheJpaEntity(
        liberacao = parent,
        numeroSequenciaLiberacao = detalhe.numeroSequenciaLiberacao,
        classificacaoItem = detalhe.classificacaoItem,
        codigoGrupo = detalhe.codigoGrupo,
        codigoSubGrupo = detalhe.codigoSubGrupo,
        codigoProduto = detalhe.codigoProduto,
        descricaoItemLiberacao = detalhe.descricaoItemLiberacao,
        quantidadeNoPedido = detalhe.quantidadeNoPedido,
        totalSeparado = detalhe.totalSeparado,
        quantidadeRestante = detalhe.quantidadeRestante,
        totalValorLiquido = detalhe.totalValorLiquido,
        totalValorBruto = detalhe.totalValorBruto,
        percentualDesconto = detalhe.percentualDesconto,
        totalCusto = detalhe.totalCusto,
        percentualDeAcrescimo = detalhe.percentualDeAcrescimo,
        precoVendaItem = detalhe.precoVendaItem,
        precoPraticado = detalhe.precoPraticado,
        custoPraticado = detalhe.custoPraticado,
    )
}
