package br.com.vertice.emerion_dashboard.application.icms.ingestion

import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsBatchCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsCommand
import br.com.vertice.emerion_dashboard.domain.icms.Icms
import br.com.vertice.emerion_dashboard.application.outbound.port.IcmsRepositoryPort
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome
import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class IngestIcmsService(
    private val icmsRepository: IcmsRepositoryPort,
    private val clock: Clock = Clock.systemUTC(),
    private val sqsProducer: SQSProducer,
    @Value($$"${app.aws.queues.ingestion-requests.icms-batch}") private val batchQueueUrl: String,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun ingest(command: IngestIcmsBatchCommand): IngestBatchResult {
        sqsProducer.sendMessageBatch(batchQueueUrl, command.items)
        logger.info("ICMS batch '{}' queued with {} item(s)", command.batchId, command.items.size)
        return IngestBatchResult(
            command.batchId,
            command.items.map { IngestItemResult(key(it), IngestOutcome.QUEUED, null) },
        )
    }

    @Transactional
    fun ingestSingle(command: IngestIcmsCommand) = ingestItem(command, Instant.now(clock))

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
