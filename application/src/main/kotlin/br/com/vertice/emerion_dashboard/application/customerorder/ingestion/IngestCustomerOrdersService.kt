package br.com.vertice.emerion_dashboard.application.customerorder.ingestion

import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderItemCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestOutcome
import br.com.vertice.emerion_dashboard.application.notification.creation.CreateNotificationUseCase
import br.com.vertice.emerion_dashboard.application.notification.creation.model.CreateNotificationCommand
import br.com.vertice.emerion_dashboard.domain.cognitouser.repository.CognitoUserRepository
import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrder
import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrderItem
import br.com.vertice.emerion_dashboard.domain.customerorder.repository.CustomerOrderRepository
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationCategory
import br.com.vertice.emerion_dashboard.domain.notification.model.NotificationPriority
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

/**
 * Use-case implementation: upserts each item in the batch by externalId
 * (the legacy Firebird numres) so re-sending the same batch from
 * emerion-load-service is a no-op (idempotent). The itens list is replaced
 * wholesale on every ingestion. A failure on one item does not abort the
 * rest of the batch.
 */
@Service
class IngestCustomerOrdersService(
    private val customerOrderRepository: CustomerOrderRepository,
    private val cognitoUserRepository: CognitoUserRepository,
    private val createNotificationUseCase: CreateNotificationUseCase,
    private val clock: Clock = Clock.systemUTC(),
) : IngestCustomerOrdersUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun ingest(command: IngestBatchCommand): IngestBatchResult {
        logger.info("Ingesting batch '{}' with {} customer order(s)", command.batchId, command.items.size)

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
    override fun ingestSingle(command: IngestCustomerOrderCommand): IngestItemResult {
        logger.info("Ingesting single customer order externalId='{}'", command.externalId)
        return ingestItem(command, Instant.now(clock))
    }

    private fun ingestItem(item: IngestCustomerOrderCommand, now: Instant): IngestItemResult {
        return try {
            val existing = customerOrderRepository.findByExternalId(item.externalId)
            val itens = item.itens.map { it.toDomain() }
            val toSave = existing?.mergeFromIngestion(
                codCli = item.codCli,
                cnpjEmpresa = item.cnpjEmpresa,
                cpfCnpj = item.cpfCnpj,
                nronfe = item.nronfe,
                dteres = item.dteres,
                sitres = item.sitres,
                totger = item.totger,
                totres = item.totres,
                totipi = item.totipi,
                totsub = item.totsub,
                totdescinc = item.totdescinc,
                itens = itens,
                now = now,
            ) ?: CustomerOrder.newFromIngestion(
                externalId = item.externalId,
                codCli = item.codCli,
                cnpjEmpresa = item.cnpjEmpresa,
                cpfCnpj = item.cpfCnpj,
                nronfe = item.nronfe,
                dteres = item.dteres,
                sitres = item.sitres,
                totger = item.totger,
                totres = item.totres,
                totipi = item.totipi,
                totsub = item.totsub,
                totdescinc = item.totdescinc,
                itens = itens,
                now = now,
            )
            val savedOrder = customerOrderRepository.save(toSave)
            if (existing == null) {
                notifyActiveUsersAboutNewOrder(savedOrder)
            }
            IngestItemResult(
                externalId = item.externalId,
                outcome = if (existing == null) IngestOutcome.CREATED else IngestOutcome.UPDATED,
                errorMessage = null,
            )
        } catch (ex: Exception) {
            logger.error("Failed to ingest customer order externalId='{}'", item.externalId, ex)
            IngestItemResult(
                externalId = item.externalId,
                outcome = IngestOutcome.FAILED,
                errorMessage = ex.message,
            )
        }
    }

    private fun notifyActiveUsersAboutNewOrder(order: CustomerOrder) {
        val activeUsers = try {
            cognitoUserRepository.findAll().filter { it.enabled }
        } catch (ex: Exception) {
            logger.error("Failed to load active users for order notification externalId='{}'", order.externalId, ex)
            return
        }

        val referenceId = order.id?.toString() ?: order.externalId
        activeUsers.forEach { user ->
            try {
                createNotificationUseCase.create(
                    CreateNotificationCommand(
                        userId = user.sub,
                        name = "Novo pedido recebido",
                        description = "Pedido ${order.externalId} foi ingerido e está disponível para consulta.",
                        category = NotificationCategory.INGESTION,
                        priority = NotificationPriority.MEDIUM,
                        referenceType = "CustomerOrder",
                        referenceId = referenceId,
                    ),
                )
            } catch (ex: Exception) {
                logger.error(
                    "Failed to create notification for user='{}' and order externalId='{}'",
                    user.sub,
                    order.externalId,
                    ex,
                )
            }
        }
    }

    private fun IngestCustomerOrderItemCommand.toDomain() = CustomerOrderItem(
        produto = produto,
        descricao = descricao,
        quantidade = quantidade,
        valorUnitario = valorUnitario,
        valorTotal = valorTotal,
        seqRe2 = seqRe2,
        codClp = codClp,
        codSt1 = codSt1,
        codUnd = codUnd,
        vluRe2 = vluRe2,
        dscRe2 = dscRe2,
        dsrRe2 = dsrRe2,
        icmsAliquota = icmsAliquota,
        icmsBase = icmsBase,
        icmsValor = icmsValor,
        icmsReducaoBase = icmsReducaoBase,
        icmsSubstituicaoBase = icmsSubstituicaoBase,
        icmsSubstituicaoValor = icmsSubstituicaoValor,
        icmsSubstituicaoAliquota = icmsSubstituicaoAliquota,
        icmsSubstituicaoMargem = icmsSubstituicaoMargem,
        icmsSubstituicaoReducaoBase = icmsSubstituicaoReducaoBase,
        ipiAliquota = ipiAliquota,
        ipiBase = ipiBase,
        ipiValor = ipiValor,
        ipiClassificacao = ipiClassificacao,
        ipiCst = ipiCst,
        pisBase = pisBase,
        pisAliquota = pisAliquota,
        pisValor = pisValor,
        pisCst = pisCst,
        cofinsBase = cofinsBase,
        cofinsAliquota = cofinsAliquota,
        cofinsValor = cofinsValor,
        cofinsCst = cofinsCst,
        descontoValor = descontoValor,
        freteValor = freteValor,
        seguroValor = seguroValor,
        outrasDespesasValor = outrasDespesasValor,
        totalItemTributado = totalItemTributado,
        totRen = totRen,
        totGe2 = totGe2,
        observacao = observacao,
        pedidoCompraCliente = pedidoCompraCliente,
        itemPedidoCompraCliente = itemPedidoCompraCliente,
        nroRe2 = nroRe2,
        flgVal = flgVal,
        flgPac = flgPac,
        flgLib = flgLib,
        codCfo = codCfo,
    )
}
