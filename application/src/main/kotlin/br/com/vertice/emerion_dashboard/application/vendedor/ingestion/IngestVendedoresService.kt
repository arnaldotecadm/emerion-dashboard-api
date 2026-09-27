package br.com.vertice.emerion_dashboard.application.vendedor.ingestion

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestVendedorCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome
import br.com.vertice.emerion_dashboard.domain.vendedor.model.Vendedor
import br.com.vertice.emerion_dashboard.domain.vendedor.repository.VendedorRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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
class IngestVendedoresService(
    private val vendedorRepository: VendedorRepository,
    private val clock: Clock = Clock.systemUTC(),
    private val sqsProducer: SQSProducer,
    @Value($$"${app.aws.queues.ingestion-requests.vendedor-batch}") private val batchQueueUrl: String
) : IngestVendedoresUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun ingest(command: IngestBatchCommand): IngestBatchResult {
        logger.info("Ingesting batch '{}' with {} vendedor(es)", command.batchId, command.items.size)

        sqsProducer.sendMessageBatch(batchQueueUrl, command.items)
        //val results = command.items.map { item -> ingestItem(item, Instant.now(clock)) }

        logger.info(
            "Ingested batch '{}' with {} vendedor(es)",
            command.batchId,
            command.items.size
        )
        return IngestBatchResult(batchId = command.batchId, results = command.items.map { item ->
            IngestItemResult(
                externalId = item.externalId,
                outcome = IngestOutcome.QUEUED,
                errorMessage = null
            )
        })
    }

    @Transactional
    override fun ingestSingle(command: IngestVendedorCommand): IngestItemResult {
        logger.info("Ingesting single vendedor externalId='{}'", command.externalId)
        return ingestItem(command, Instant.now(clock))
    }

    private fun ingestItem(item: IngestVendedorCommand, now: Instant): IngestItemResult {
        return try {
            val existing = vendedorRepository.findByExternalId(item.externalId)
            val toSave = existing?.mergeFromIngestion(
                cnpjEmpresa = item.cnpjEmpresa,
                nome = item.nome,
                apelido = item.apelido,
                cpfCnpj = item.cpfCnpj,
                telefone = item.telefone,
                celular = item.celular,
                email = item.email,
                cidade = item.cidade,
                uf = item.uf,
                ativo = item.ativo,
                saldo = item.saldo,
                dataCadastro = item.dataCadastro,
                now = now,
            ) ?: Vendedor.newFromIngestion(
                externalId = item.externalId,
                cnpjEmpresa = item.cnpjEmpresa,
                nome = item.nome,
                apelido = item.apelido,
                cpfCnpj = item.cpfCnpj,
                telefone = item.telefone,
                celular = item.celular,
                email = item.email,
                cidade = item.cidade,
                uf = item.uf,
                situacao = item.ativo,
                saldo = item.saldo,
                dataCadastro = item.dataCadastro,
                now = now,
            )
            vendedorRepository.save(toSave)
            IngestItemResult(
                externalId = item.externalId,
                outcome = if (existing == null) IngestOutcome.CREATED else IngestOutcome.UPDATED,
                errorMessage = null,
            )
        } catch (ex: Exception) {
            logger.error("Failed to ingest vendedor externalId='{}'", item.externalId, ex)
            IngestItemResult(
                externalId = item.externalId,
                outcome = IngestOutcome.FAILED,
                errorMessage = ex.message,
            )
        }
    }
}
