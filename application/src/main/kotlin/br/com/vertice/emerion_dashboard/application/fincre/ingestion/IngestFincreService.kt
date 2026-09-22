package br.com.vertice.emerion_dashboard.application.fincre.ingestion

import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreBatchCommand
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreCommand
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreParcelaCommand
import br.com.vertice.emerion_dashboard.domain.fincre.model.Fincre
import br.com.vertice.emerion_dashboard.domain.fincre.model.FincreParcela
import br.com.vertice.emerion_dashboard.domain.fincre.repository.FincreRepository
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class IngestFincreService(
    private val fincreRepository: FincreRepository,
    private val clock: Clock = Clock.systemUTC(),
) : IngestFincreUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun ingest(command: IngestFincreBatchCommand): IngestBatchResult {
        val results = command.items.map { ingestItem(it, Instant.now(clock)) }
        logger.info(
            "FINCRE batch '{}' processed: {} succeeded, {} failed",
            command.batchId,
            results.count { it.outcome != IngestOutcome.FAILED },
            results.count { it.outcome == IngestOutcome.FAILED },
        )
        return IngestBatchResult(command.batchId, results)
    }

    @Transactional
    override fun ingestSingle(command: IngestFincreCommand): IngestItemResult =
        ingestItem(command, Instant.now(clock))

    private fun ingestItem(item: IngestFincreCommand, now: Instant): IngestItemResult = try {
        val existing = fincreRepository.findByCnpjEmpresaAndDocumento(item.cnpjEmpresa, item.documento)
        val parcelas = item.parcelas.map { it.toDomain() }
        val toSave = existing?.mergeFromIngestion(
            codigoEmpresa = item.codigoEmpresa,
            dataEmissao = item.dataEmissao,
            codigoCondicaoRecebimento = item.codigoCondicaoRecebimento,
            nomeCondicaoRecebimento = item.nomeCondicaoRecebimento,
            nomeEmpresa = item.nomeEmpresa,
            codigoComissao = item.codigoComissao,
            percentualComissao = item.percentualComissao,
            codigoCliente = item.codigoCliente,
            nomeCliente = item.nomeCliente,
            codigoVendedor = item.codigoVendedor,
            nomeVendedor = item.nomeVendedor,
            codigoTipoDocumento = item.codigoTipoDocumento,
            nomeTipoDocumento = item.nomeTipoDocumento,
            parcelas = parcelas,
            now = now,
        ) ?: Fincre.newFromIngestion(
            cnpjEmpresa = item.cnpjEmpresa,
            codigoEmpresa = item.codigoEmpresa,
            dataEmissao = item.dataEmissao,
            documento = item.documento,
            codigoCondicaoRecebimento = item.codigoCondicaoRecebimento,
            nomeCondicaoRecebimento = item.nomeCondicaoRecebimento,
            nomeEmpresa = item.nomeEmpresa,
            codigoComissao = item.codigoComissao,
            percentualComissao = item.percentualComissao,
            codigoCliente = item.codigoCliente,
            nomeCliente = item.nomeCliente,
            codigoVendedor = item.codigoVendedor,
            nomeVendedor = item.nomeVendedor,
            codigoTipoDocumento = item.codigoTipoDocumento,
            nomeTipoDocumento = item.nomeTipoDocumento,
            parcelas = parcelas,
            now = now,
        )
        fincreRepository.save(toSave)
        IngestItemResult(
            key(item),
            if (existing == null) IngestOutcome.CREATED else IngestOutcome.UPDATED,
            null
        )
    } catch (ex: Exception) {
        logger.error("Failed to ingest FINCRE cnpjEmpresa='{}', documento='{}'", item.cnpjEmpresa, item.documento, ex)
        IngestItemResult(key(item), IngestOutcome.FAILED, ex.message)
    }

    private fun IngestFincreParcelaCommand.toDomain() = FincreParcela(
        numeroParcela = numeroParcela,
        flagIncobravel = flagIncobravel,
        dataIncobravel = dataIncobravel,
        dataVencimento = dataVencimento,
        prazoEmDias = prazoEmDias,
        valorParcela = valorParcela,
        numeroBancario = numeroBancario,
        codigoBanco = codigoBanco,
        nomeBanco = nomeBanco,
        observacoes = observacoes,
        flagCartaAnuencia = flagCartaAnuencia,
        dataCartaAnuencia = dataCartaAnuencia,
        flagPago = flagPago,
    )

    private fun key(item: IngestFincreCommand) = "${item.cnpjEmpresa}:${item.documento}"
}
