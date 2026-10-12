package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.IngestIpiService
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiCommand
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class SqsIpiConsumer(
    private val objectMapper: ObjectMapper,
    private val sqsProducer: SQSProducer,
    private val ingestIpiService: IngestIpiService,
    @Value($$"${app.aws.queues.ingestion-requests.ipi}") private val queueUrl: String,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener("emerion-dashboard-ipi-batch")
    fun receiveMessage(message: String) {
        val items: List<IngestIpiCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(List::class.java, IngestIpiCommand::class.java)
            )

        logger.info("Received {} IPI rules, sending them to the ingestion queue", items.size)
        items.forEach { sqsProducer.sendMessage(queueUrl, it) }
    }

    @SqsListener(
        "emerion-dashboard-ipi",
        maxConcurrentMessages = "3",
        maxMessagesPerPoll = "3",
    )
    fun receiveMessage(message: IngestIpiCommand) {
        ingestIpiService.ingestSingle(message)
    }
}
