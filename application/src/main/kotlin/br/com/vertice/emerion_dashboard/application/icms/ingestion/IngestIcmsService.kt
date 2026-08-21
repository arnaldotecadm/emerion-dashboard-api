package br.com.vertice.emerion_dashboard.application.icms.ingestion

import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsBatchCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsBatchResult
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsItemResult
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsOutcome
import br.com.vertice.emerion_dashboard.domain.icms.model.Icms
import br.com.vertice.emerion_dashboard.domain.icms.repository.IcmsRepository
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
    override fun ingest(command: IngestIcmsBatchCommand): IngestIcmsBatchResult {
        val results = command.items.map { ingestItem(it, Instant.now(clock)) }
        logger.info("ICMS batch '{}' processed: {} succeeded, {} failed", command.batchId,
            results.count { it.outcome != IngestIcmsOutcome.FAILED }, results.count { it.outcome == IngestIcmsOutcome.FAILED })
        return IngestIcmsBatchResult(command.batchId, results)
    }

    @Transactional
    override fun ingestSingle(command: IngestIcmsCommand) = ingestItem(command, Instant.now(clock))

    private fun ingestItem(item: IngestIcmsCommand, now: Instant): IngestIcmsItemResult = try {
        val existing = icmsRepository.findByCnpjEmpresaAndCodigoIcms(item.cnpjEmpresa, item.codigoIcms)
        val toSave = existing?.mergeFromIngestion(
            item.tipoIcms, item.nomeIcms, item.ufEmitente, item.codigoRegimeTributario, item.aliquotaIcms,
            item.percentualReducaoValorImposto, item.percentualBaseCalculoIcms, item.situacaoTributariaIcms, now,
        ) ?: Icms(
            id = null, cnpjEmpresa = item.cnpjEmpresa, codigoIcms = item.codigoIcms, tipoIcms = item.tipoIcms,
            nomeIcms = item.nomeIcms, ufEmitente = item.ufEmitente, codigoRegimeTributario = item.codigoRegimeTributario,
            aliquotaIcms = item.aliquotaIcms, percentualReducaoValorImposto = item.percentualReducaoValorImposto,
            percentualBaseCalculoIcms = item.percentualBaseCalculoIcms, situacaoTributariaIcms = item.situacaoTributariaIcms,
            createdAt = now, updatedAt = now,
        )
        icmsRepository.save(toSave)
        IngestIcmsItemResult(key(item), if (existing == null) IngestIcmsOutcome.CREATED else IngestIcmsOutcome.UPDATED, null)
    } catch (ex: Exception) {
        logger.error("Failed to ingest ICMS cnpjEmpresa='{}', codigoIcms='{}'", item.cnpjEmpresa, item.codigoIcms, ex)
        IngestIcmsItemResult(key(item), IngestIcmsOutcome.FAILED, ex.message)
    }

    private fun key(item: IngestIcmsCommand) = "${item.cnpjEmpresa}:${item.codigoIcms}"
}
