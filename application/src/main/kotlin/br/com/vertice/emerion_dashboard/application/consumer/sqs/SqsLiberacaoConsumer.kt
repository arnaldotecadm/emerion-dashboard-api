package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.IngestLiberacaoService
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class SqsLiberacaoConsumer(
    private val objectMapper: ObjectMapper,
    private val sqsProducer: SQSProducer,
    private val ingestionService: IngestLiberacaoService,
    @Value($$"${app.aws.queues.ingestion-requests.pedlib}") private val queueUrl: String,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener($$"${app.aws.queues.ingestion-requests.pedlib-batch}")
    fun receiveBatch(message: String) {
        val items: List<IngestLiberacaoCommand> = objectMapper.readValue(
            message,
            objectMapper.typeFactory.constructCollectionType(List::class.java, IngestLiberacaoCommand::class.java),
        )
        logger.info("Forwarding {} PEDLIB release(s) to the ingestion queue", items.size)
        items.forEach { item ->
            try {
                sqsProducer.sendMessage(queueUrl, item)
            } catch (ex: Exception) {
                logger.error("Failed to queue PEDLIB release '{}:{}'", item.numeroPedido, item.numeroLiberacao, ex)
                throw ex
            }
        }
    }

    @SqsListener($$"${app.aws.queues.ingestion-requests.pedlib}")
    fun receiveSingle(message: IngestLiberacaoCommand) {
        val result = ingestionService.ingestSingle(message)
        check(result.outcome != IngestOutcome.FAILED) { "PEDLIB ingestion failed; message will be retried" }
    }
}
