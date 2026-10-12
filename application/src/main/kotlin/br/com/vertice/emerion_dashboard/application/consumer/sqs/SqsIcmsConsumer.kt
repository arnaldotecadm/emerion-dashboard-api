package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.icms.ingestion.IngestIcmsService
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsCommand
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class SqsIcmsConsumer(
    private val objectMapper: ObjectMapper,
    private val sqsProducer: SQSProducer,
    private val ingestIcmsService: IngestIcmsService,
    @Value($$"${app.aws.queues.ingestion-requests.icms}") private val queueUrl: String,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener("emerion-dashboard-icms-batch")
    fun receiveMessage(message: String) {
        val items: List<IngestIcmsCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(List::class.java, IngestIcmsCommand::class.java)
            )

        logger.info("Received {} ICMS rules, sending them to the ingestion queue", items.size)
        items.forEach { sqsProducer.sendMessage(queueUrl, it) }
    }

    @SqsListener(
        "emerion-dashboard-icms",
        maxConcurrentMessages = "3",
        maxMessagesPerPoll = "3",
    )
    fun receiveMessage(message: IngestIcmsCommand) {
        ingestIcmsService.ingestSingle(message)
    }
}
