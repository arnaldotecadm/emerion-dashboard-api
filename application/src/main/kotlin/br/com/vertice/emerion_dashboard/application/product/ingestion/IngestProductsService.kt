package br.com.vertice.emerion_dashboard.application.product.ingestion

import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestOutcome
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestProductCommand
import br.com.vertice.emerion_dashboard.domain.product.model.Product
import br.com.vertice.emerion_dashboard.domain.product.repository.ProductRepository
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
class IngestProductsService(
    private val productRepository: ProductRepository,
    private val clock: Clock = Clock.systemUTC(),
) : IngestProductsUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun ingest(command: IngestBatchCommand): IngestBatchResult {
        logger.info("Ingesting batch '{}' with {} product(s)", command.batchId, command.items.size)

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
    override fun ingestSingle(command: IngestProductCommand): IngestItemResult {
        logger.info("Ingesting single product externalId='{}'", command.externalId)
        return ingestItem(command, Instant.now(clock))
    }

    private fun ingestItem(item: IngestProductCommand, now: Instant): IngestItemResult {
        return try {
            val existing = productRepository.findByExternalId(item.externalId)
            val toSave = existing?.mergeFromIngestion(
                nome = item.nome,
                preco = item.preco,
                now = now,
            ) ?: Product.newFromIngestion(
                externalId = item.externalId,
                nome = item.nome,
                preco = item.preco,
                now = now,
            )
            productRepository.save(toSave)
            IngestItemResult(
                externalId = item.externalId,
                outcome = if (existing == null) IngestOutcome.CREATED else IngestOutcome.UPDATED,
                errorMessage = null,
            )
        } catch (ex: Exception) {
            logger.error("Failed to ingest product externalId='{}'", item.externalId, ex)
            IngestItemResult(
                externalId = item.externalId,
                outcome = IngestOutcome.FAILED,
                errorMessage = ex.message,
            )
        }
    }
}
