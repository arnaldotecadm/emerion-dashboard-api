package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.IngestCustomerOrdersService
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderCommand
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class SqsCustomerOrdersConsumer(
    private val objectMapper: ObjectMapper,
    private val sqsProducer: SQSProducer,
    private val ingestionService: IngestCustomerOrdersService,
    @Value($$"${app.aws.queues.ingestion-requests.customer-orders}") private val queueUrl: String,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener(
        "emerion-dashboard-customer-orders-batch",
        maxConcurrentMessages = $$"${app.sqs.fan-out.max-concurrent-messages:5}",
    )
    fun receiveMessage(message: String) {
        val customerOrders: List<IngestCustomerOrderCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(
                    List::class.java,
                    IngestCustomerOrderCommand::class.java
                )
            )

        logger.info("Received {} customer orders, sending them to the ingestion queue", customerOrders.size)
        sqsProducer.sendMessages(queueUrl, customerOrders)
    }

    @SqsListener(
        "emerion-dashboard-customer-orders",
        maxConcurrentMessages = $$"${app.sqs.ingestion.max-concurrent-messages:20}",
    )
    fun receiveMessage(message: IngestCustomerOrderCommand) {
        ingestionService.ingestSingle(message)
    }
}