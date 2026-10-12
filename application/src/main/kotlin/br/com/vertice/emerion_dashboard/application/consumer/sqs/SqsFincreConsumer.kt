package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.IngestFincreService
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreCommand
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class SqsFincreConsumer(
    private val objectMapper: ObjectMapper,
    private val sqsProducer: SQSProducer,
    private val ingestFincreService: IngestFincreService,
    @Value($$"${app.aws.queues.ingestion-requests.fincre}") private val queueUrl: String,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener("emerion-dashboard-fincre-batch")
    fun receiveMessage(message: String) {
        val items: List<IngestFincreCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(List::class.java, IngestFincreCommand::class.java)
            )

        logger.info("Received {} FINCRE titles, sending them to the ingestion queue", items.size)
        items.forEach { sqsProducer.sendMessage(queueUrl, it) }
    }

    @SqsListener(
        "emerion-dashboard-fincre",
        maxConcurrentMessages = "3",
        maxMessagesPerPoll = "3",
    )
    fun receiveMessage(message: IngestFincreCommand) {
        ingestFincreService.ingestSingle(message)
    }
}
