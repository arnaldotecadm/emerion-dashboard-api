package br.com.vertice.emerion_dashboard.application.customer

import br.com.vertice.emerion_dashboard.domain.customer.Customer
import br.com.vertice.emerion_dashboard.domain.customer.CustomerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

/**
 * Use-case implementation: upserts each item in the batch by externalId so
 * re-sending the same batch from emerion-load-service is a no-op (idempotent).
 * A failure on one item does not abort the rest of the batch.
 */
@Service
class IngestCustomersService(
    private val customerRepository: CustomerRepository,
    private val clock: Clock = Clock.systemUTC(),
) : IngestCustomersUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun ingest(command: IngestBatchCommand): IngestBatchResult {
        logger.info("Ingesting batch '{}' with {} customer(s)", command.batchId, command.items.size)

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
    override fun ingestSingle(command: IngestCustomerCommand): IngestItemResult {
        logger.info("Ingesting single customer externalId='{}'", command.externalId)
        return ingestItem(command, Instant.now(clock))
    }

    private fun ingestItem(item: IngestCustomerCommand, now: Instant): IngestItemResult {
        return try {
            val existing = customerRepository.findByExternalId(item.externalId)
            val toSave = existing?.mergeFromIngestion(
                nomeFantasia = item.nomeFantasia,
                razaoSocial = item.razaoSocial,
                cpfCnpj = item.cpfCnpj,
                inscricaoEstadual = item.inscricaoEstadual,
                regimeTributario = item.regimeTributario,
                bloqueado = item.bloqueado,
                now = now,
            ) ?: Customer.newFromIngestion(
                externalId = item.externalId,
                nomeFantasia = item.nomeFantasia,
                razaoSocial = item.razaoSocial,
                cpfCnpj = item.cpfCnpj,
                inscricaoEstadual = item.inscricaoEstadual,
                regimeTributario = item.regimeTributario,
                bloqueado = item.bloqueado,
                createdAt = item.createdAt,
                now = now,
            )
            customerRepository.save(toSave)
            IngestItemResult(
                externalId = item.externalId,
                outcome = if (existing == null) IngestOutcome.CREATED else IngestOutcome.UPDATED,
                errorMessage = null,
            )
        } catch (ex: Exception) {
            logger.error("Failed to ingest customer externalId='{}'", item.externalId, ex)
            IngestItemResult(
                externalId = item.externalId,
                outcome = IngestOutcome.FAILED,
                errorMessage = ex.message,
            )
        }
    }
}
