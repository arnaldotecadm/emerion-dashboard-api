package br.com.vertice.emerion_dashboard.application.liberacao.ingestion

import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoCommand
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoDetalheCommand
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoItemResult
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoOutcome
import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.model.LiberacaoDetalhe
import br.com.vertice.emerion_dashboard.domain.liberacao.repository.LiberacaoRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class IngestLiberacaoService(
    private val liberacaoRepository: LiberacaoRepository,
    private val clock: Clock = Clock.systemUTC(),
) : IngestLiberacaoUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun ingestSingle(command: IngestLiberacaoCommand): IngestLiberacaoItemResult {
        val key = key(command)
        return try {
            val now = Instant.now(clock)
            val existing = liberacaoRepository.findByNumeroPedidoAndNumeroLiberacao(
                command.numeroPedido,
                command.numeroLiberacao,
            )
            val detalhes = command.detalhes.map { it.toDomain() }
            val toSave = existing?.mergeFromIngestion(
                cnpjEmpresa = command.cnpjEmpresa,
                codigoEmpresa = command.codigoEmpresa,
                dataPedido = command.dataPedido,
                dataLiberacao = command.dataLiberacao,
                horaLiberacao = command.horaLiberacao,
                codigoCliente = command.codigoCliente,
                quantidadeSeparada = command.quantidadeSeparada,
                totalLiberadoSemImpostos = command.totalLiberadoSemImpostos,
                totalLiberadoComImpostos = command.totalLiberadoComImpostos,
                situacaoLiberacao = command.situacaoLiberacao,
                codigoVendedor = command.codigoVendedor,
                comissaoLiberacao = command.comissaoLiberacao,
                totalCusto = command.totalCusto,
                detalhes = detalhes,
                now = now,
            ) ?: Liberacao.newFromIngestion(
                cnpjEmpresa = command.cnpjEmpresa,
                codigoEmpresa = command.codigoEmpresa,
                dataPedido = command.dataPedido,
                numeroPedido = command.numeroPedido,
                numeroLiberacao = command.numeroLiberacao,
                dataLiberacao = command.dataLiberacao,
                horaLiberacao = command.horaLiberacao,
                codigoCliente = command.codigoCliente,
                quantidadeSeparada = command.quantidadeSeparada,
                totalLiberadoSemImpostos = command.totalLiberadoSemImpostos,
                totalLiberadoComImpostos = command.totalLiberadoComImpostos,
                situacaoLiberacao = command.situacaoLiberacao,
                codigoVendedor = command.codigoVendedor,
                comissaoLiberacao = command.comissaoLiberacao,
                totalCusto = command.totalCusto,
                detalhes = detalhes,
                now = now,
            )
            liberacaoRepository.save(toSave)
            IngestLiberacaoItemResult(
                externalId = key,
                outcome = if (existing == null) IngestLiberacaoOutcome.CREATED else IngestLiberacaoOutcome.UPDATED,
                errorMessage = null,
            )
        } catch (ex: Exception) {
            logger.error("Failed to ingest PEDLIB release '{}'", key, ex)
            IngestLiberacaoItemResult(key, IngestLiberacaoOutcome.FAILED, ex.message)
        }
    }

    private fun IngestLiberacaoDetalheCommand.toDomain() = LiberacaoDetalhe(
        numeroSequenciaLiberacao = numeroSequenciaLiberacao,
        classificacaoItem = classificacaoItem,
        codigoGrupo = codigoGrupo,
        codigoSubGrupo = codigoSubGrupo,
        codigoProduto = codigoProduto,
        descricaoItemLiberacao = descricaoItemLiberacao,
        quantidadeNoPedido = quantidadeNoPedido,
        totalSeparado = totalSeparado,
        quantidadeRestante = quantidadeRestante,
        totalValorLiquido = totalValorLiquido,
        totalValorBruto = totalValorBruto,
        percentualDesconto = percentualDesconto,
        totalCusto = totalCusto,
        percentualDeAcrescimo = percentualDeAcrescimo,
        precoVendaItem = precoVendaItem,
        precoPraticado = precoPraticado,
        custoPraticado = custoPraticado,
    )

    private fun key(command: IngestLiberacaoCommand) = "${command.numeroPedido}:${command.numeroLiberacao}"
}
