package br.com.vertice.emerion_dashboard.application.icms.ingestion

import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsBatchCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsCommand
import br.com.vertice.emerion_dashboard.domain.icms.model.Icms
import br.com.vertice.emerion_dashboard.domain.icms.repository.IcmsRepository
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class IngestIcmsService(
    private val icmsRepository: IcmsRepository,
    private val clock: Clock = Clock.systemUTC(),
) : IngestIcmsUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun ingest(command: IngestIcmsBatchCommand): IngestBatchResult {
        val results = command.items.map { ingestItem(it, Instant.now(clock)) }
        logger.info(
            "ICMS batch '{}' processed: {} succeeded, {} failed", command.batchId,
            results.count { it.outcome != IngestOutcome.FAILED }, results.count { it.outcome == IngestOutcome.FAILED })
        return IngestBatchResult(command.batchId, results)
    }

    @Transactional
    override fun ingestSingle(command: IngestIcmsCommand) = ingestItem(command, Instant.now(clock))

    private fun ingestItem(item: IngestIcmsCommand, now: Instant): IngestItemResult = try {
        val existing = icmsRepository.findByCnpjEmpresaAndCodigoIcms(item.cnpjEmpresa, item.codigoIcms)
        val toSave = existing?.mergeFromIngestion(
            item.tipoIcms, item.nomeIcms, item.ufEmitente, item.codigoRegimeTributario, item.aliquotaIcms,
            item.percentualReducaoValorImposto, item.percentualBaseCalculoIcms, item.situacaoTributariaIcms, now,
        ) ?: Icms(
            id = null,
            cnpjEmpresa = item.cnpjEmpresa,
            codigoIcms = item.codigoIcms,
            tipoIcms = item.tipoIcms,
            nomeIcms = item.nomeIcms,
            ufEmitente = item.ufEmitente,
            codigoRegimeTributario = item.codigoRegimeTributario,
            aliquotaIcms = item.aliquotaIcms,
            percentualReducaoValorImposto = item.percentualReducaoValorImposto,
            percentualBaseCalculoIcms = item.percentualBaseCalculoIcms,
            situacaoTributariaIcms = item.situacaoTributariaIcms,
            createdAt = now,
            updatedAt = now,
        )
        icmsRepository.save(toSave)
        IngestItemResult(
            key(item),
            if (existing == null) IngestOutcome.CREATED else IngestOutcome.UPDATED,
            null
        )
    } catch (ex: Exception) {
        logger.error("Failed to ingest ICMS cnpjEmpresa='{}', codigoIcms='{}'", item.cnpjEmpresa, item.codigoIcms, ex)
        IngestItemResult(key(item), IngestOutcome.FAILED, ex.message)
    }

    private fun key(item: IngestIcmsCommand) = "${item.cnpjEmpresa}:${item.codigoIcms}"
}
