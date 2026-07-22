package br.com.vertice.emerion_dashboard.application.customeraddress.ingestion

import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressDetailCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestOutcome
import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddress
import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddressDetail
import br.com.vertice.emerion_dashboard.domain.customeraddress.repository.CustomerAddressRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

/**
 * Use-case implementation: upserts each item in the batch by externalId
 * (the customer's legacy codCli) so re-sending the same batch from
 * emerion-load-service is a no-op (idempotent). The enderecos list is
 * replaced wholesale on every ingestion. A failure on one item does not
 * abort the rest of the batch.
 */
@Service
class IngestCustomerAddressesService(
    private val customerAddressRepository: CustomerAddressRepository,
    private val clock: Clock = Clock.systemUTC(),
) : IngestCustomerAddressesUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun ingest(command: IngestBatchCommand): IngestBatchResult {
        logger.info("Ingesting batch '{}' with {} customer address set(s)", command.batchId, command.items.size)

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
    override fun ingestSingle(command: IngestCustomerAddressCommand): IngestItemResult {
        logger.info("Ingesting single customer address set externalId='{}'", command.externalId)
        return ingestItem(command, Instant.now(clock))
    }

    private fun ingestItem(item: IngestCustomerAddressCommand, now: Instant): IngestItemResult {
        return try {
            val existing = customerAddressRepository.findByExternalId(item.externalId)
            val enderecos = item.enderecos.map { it.toDomain() }
            val toSave = existing?.mergeFromIngestion(
                cnpjEmpresa = item.cnpjEmpresa,
                cpfCnpj = item.cpfCnpj,
                enderecos = enderecos,
                now = now,
            ) ?: CustomerAddress.newFromIngestion(
                externalId = item.externalId,
                cnpjEmpresa = item.cnpjEmpresa,
                cpfCnpj = item.cpfCnpj,
                enderecos = enderecos,
                now = now,
            )
            customerAddressRepository.save(toSave)
            IngestItemResult(
                externalId = item.externalId,
                outcome = if (existing == null) IngestOutcome.CREATED else IngestOutcome.UPDATED,
                errorMessage = null,
            )
        } catch (ex: Exception) {
            logger.error("Failed to ingest customer address set externalId='{}'", item.externalId, ex)
            IngestItemResult(
                externalId = item.externalId,
                outcome = IngestOutcome.FAILED,
                errorMessage = ex.message,
            )
        }
    }

    private fun IngestCustomerAddressDetailCommand.toDomain() = CustomerAddressDetail(
        tipo = tipo,
        cep = cep,
        endereco = endereco,
        numero = numero,
        referencia = referencia,
        bairro = bairro,
        cidade = cidade,
        uf = uf,
        telefone = telefone,
        telefoneContato = telefoneContato,
        complemento = complemento,
        fax = fax,
    )
}
