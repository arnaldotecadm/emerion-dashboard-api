package br.com.vertice.emerion_dashboard.application.customercredit.ingestion

import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestCustomerCreditCommand
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestOutcome
import br.com.vertice.emerion_dashboard.domain.customercredit.model.CustomerCredit
import br.com.vertice.emerion_dashboard.domain.customercredit.repository.CustomerCreditRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

/**
 * Use-case implementation: upserts each item in the batch by
 * (customerExternalId, sequencia) so re-sending the same batch from
 * emerion-load-service is a no-op (idempotent) whenever sequencia is
 * present. Entries with a null sequencia have no reliable key and are
 * always inserted as new rows. A failure on one item does not abort the
 * rest of the batch.
 */
@Service
class IngestCustomerCreditsService(
    private val customerCreditRepository: CustomerCreditRepository,
    private val clock: Clock = Clock.systemUTC(),
) : IngestCustomerCreditsUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun ingest(command: IngestBatchCommand): IngestBatchResult {
        logger.info("Ingesting batch '{}' with {} customer credit entr(y/ies)", command.batchId, command.items.size)

        val results = command.items.map { item -> ingestItem(item, Instant.now(clock)) }

        logger.info(
            "Batch '{}' processed: {} succeeded, {} failed",
            command.batchId,
            results.count { it.outcome != IngestOutcome.FAILED },
            results.count { it.outcome == IngestOutcome.FAILED },
        )
        return IngestBatchResult(batchId = command.batchId, results = results)
    }

    @Transactional
    override fun ingestSingle(command: IngestCustomerCreditCommand): IngestItemResult {
        logger.info(
            "Ingesting single customer credit entry customerExternalId='{}', sequencia='{}'",
            command.customerExternalId,
            command.sequencia,
        )
        return ingestItem(command, Instant.now(clock))
    }

    private fun ingestItem(item: IngestCustomerCreditCommand, now: Instant): IngestItemResult {
        val itemLabel = "${item.customerExternalId}:${item.sequencia ?: "-"}"
        return try {
            val existing = item.sequencia?.let {
                customerCreditRepository.findByCustomerExternalIdAndSequencia(item.customerExternalId, it)
            }
            val toSave = existing?.mergeFromIngestion(
                data = item.data,
                dataPedido = item.dataPedido,
                valorUtilizado = item.valorUtilizado,
                valorTotal = item.valorTotal,
                saldo = item.saldo,
                situacao = item.situacao,
                tipo = item.tipo,
                now = now,
            ) ?: CustomerCredit.newFromIngestion(
                customerExternalId = item.customerExternalId,
                sequencia = item.sequencia,
                data = item.data,
                dataPedido = item.dataPedido,
                valorUtilizado = item.valorUtilizado,
                valorTotal = item.valorTotal,
                saldo = item.saldo,
                situacao = item.situacao,
                tipo = item.tipo,
                now = now,
            )
            customerCreditRepository.save(toSave)
            IngestItemResult(
                externalId = itemLabel,
                outcome = if (existing == null) IngestOutcome.CREATED else IngestOutcome.UPDATED,
                errorMessage = null,
            )
        } catch (ex: Exception) {
            logger.error("Failed to ingest customer credit entry '{}'", itemLabel, ex)
            IngestItemResult(
                externalId = itemLabel,
                outcome = IngestOutcome.FAILED,
                errorMessage = ex.message,
            )
        }
    }
}
