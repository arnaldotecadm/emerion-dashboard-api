package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.IngestVendedoresService
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestVendedorCommand
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
@Service
class SqsVendedorConsumer(
    private val objectMapper: ObjectMapper,
    private val sqsProducer: SQSProducer,
    private val ingestionService: IngestVendedoresService,
    @Value($$"${app.aws.queues.ingestion-requests.vendedor}") private val queueUrl: String,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener(
        "emerion-dashboard-vendedor-batch",
        maxConcurrentMessages = $$"${app.sqs.fan-out.max-concurrent-messages:10}",
    )
    fun receiveMessage(message: String) {
        val vendedores: List<IngestVendedorCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(
                    List::class.java,
                    IngestVendedorCommand::class.java
                )
            )

        logger.info("Received {} vendedores, sending them to the ingestion queue", vendedores.size)
        sqsProducer.sendMessages(queueUrl, vendedores)
    }

    @SqsListener(
        "emerion-dashboard-vendedor",
        maxConcurrentMessages = $$"${app.sqs.ingestion.max-concurrent-messages:20}",
    )
    fun receiveMessage(message: IngestVendedorCommand) {
        ingestionService.ingestSingle(message)
    }
}