package br.com.vertice.emerion_dashboard.application.customerquotation.ingestion

import br.com.vertice.emerion_dashboard.application.customerquotation.ingestion.model.IngestCustomerQuotationBatchCommand
import br.com.vertice.emerion_dashboard.application.customerquotation.ingestion.model.IngestCustomerQuotationCommand
import br.com.vertice.emerion_dashboard.application.outbound.port.CustomerQuotationRepositoryPort
import br.com.vertice.emerion_dashboard.domain.customerquotation.CustomerQuotation
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
class IngestCustomerQuotationService(
    private val customerQuotationRepository: CustomerQuotationRepositoryPort,
    private val clock: Clock = Clock.systemUTC(),
    private val sqsProducer: SQSProducer,
    @Value($$"${app.aws.queues.ingestion-requests.customer-quotation-batch}") private val batchQueueUrl: String,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun ingest(command: IngestCustomerQuotationBatchCommand): IngestBatchResult {
        sqsProducer.sendMessageBatch(batchQueueUrl, command.items)
        logger.info("Customer quotation batch '{}' queued with {} item(s)", command.batchId, command.items.size)
        return IngestBatchResult(
            command.batchId,
            command.items.map { IngestItemResult(key(it), IngestOutcome.QUEUED, null) },
        )
    }

    @Transactional
    fun ingestSingle(command: IngestCustomerQuotationCommand): IngestItemResult =
        ingestItem(command, Instant.now(clock))

    private fun ingestItem(item: IngestCustomerQuotationCommand, now: Instant): IngestItemResult = try {
        val existing = customerQuotationRepository.findByCnpjEmpresaAndExternalId(item.cnpjEmpresa, item.externalId)
        val toSave = existing?.mergeFromIngestion(
            codigoEmpresa = item.codigoEmpresa,
            codigoCliente = item.codigoCliente,
            customerExternalId = item.customerExternalId,
            dteres = item.dteres,
            numeroPedido = item.numeroPedido,
            dataPedido = item.dataPedido,
            totalPedidoComImpostos = item.totalPedidoComImpostos,
            totalPedidoSemImpostos = item.totalPedidoSemImpostos,
            cpfCnpj = item.cpfCnpj,
            statusPedido = item.statusPedido,
            totalIpi = item.totalIpi,
            totalIcms = item.totalIcms,
            totalPis = item.totalPis,
            totalCofins = item.totalCofins,
            totalSubstituicaoTributaria = item.totalSubstituicaoTributaria,
            totalDescontoIncondicional = item.totalDescontoIncondicional,
            totalFrete = item.totalFrete,
            totalSeguro = item.totalSeguro,
            totalOutrasDespesas = item.totalOutrasDespesas,
            vendedorExternalId = item.vendedorExternalId,
            atendenteCod = item.atendenteCod,
            regimeTributario = item.regimeTributario,
            nomeRegimeTributario = item.nomeRegimeTributario,
            codigoPadraoFaturamento = item.codigoPadraoFaturamento,
            numeroCotacao = item.numeroCotacao,
            now = now,
        ) ?: CustomerQuotation.newFromIngestion(
            externalId = item.externalId,
            cnpjEmpresa = item.cnpjEmpresa,
            codigoEmpresa = item.codigoEmpresa,
            codigoCliente = item.codigoCliente,
            customerExternalId = item.customerExternalId,
            dteres = item.dteres,
            numeroPedido = item.numeroPedido,
            dataPedido = item.dataPedido,
            totalPedidoComImpostos = item.totalPedidoComImpostos,
            totalPedidoSemImpostos = item.totalPedidoSemImpostos,
            cpfCnpj = item.cpfCnpj,
            statusPedido = item.statusPedido,
            totalIpi = item.totalIpi,
            totalIcms = item.totalIcms,
            totalPis = item.totalPis,
            totalCofins = item.totalCofins,
            totalSubstituicaoTributaria = item.totalSubstituicaoTributaria,
            totalDescontoIncondicional = item.totalDescontoIncondicional,
            totalFrete = item.totalFrete,
            totalSeguro = item.totalSeguro,
            totalOutrasDespesas = item.totalOutrasDespesas,
            vendedorExternalId = item.vendedorExternalId,
            atendenteCod = item.atendenteCod,
            regimeTributario = item.regimeTributario,
            nomeRegimeTributario = item.nomeRegimeTributario,
            codigoPadraoFaturamento = item.codigoPadraoFaturamento,
            numeroCotacao = item.numeroCotacao,
            now = now,
        )
        customerQuotationRepository.save(toSave)
        IngestItemResult(
            key(item),
            if (existing == null) IngestOutcome.CREATED else IngestOutcome.UPDATED,
            null,
        )
    } catch (ex: Exception) {
        logger.error(
            "Failed to ingest customer quotation cnpjEmpresa='{}', externalId='{}'",
            item.cnpjEmpresa,
            item.externalId,
            ex,
        )
        IngestItemResult(key(item), IngestOutcome.FAILED, ex.message)
    }

    private fun key(item: IngestCustomerQuotationCommand) = "${item.cnpjEmpresa}:${item.externalId}"
}
