package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.customer.ingestion.IngestCustomersService
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestCustomerCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.IngestCustomerOrdersService
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderCommand
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class SqsCustomerConsumer(
    private val objectMapper: ObjectMapper,
    private val sqsProducer: SQSProducer,
    private val ingestionService: IngestCustomersService,
    @Value($$"${app.aws.queues.ingestion-requests.customer}") private val queueUrl: String,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener(
        "emerion-dashboard-customer-batch",
        maxConcurrentMessages = $$"${app.sqs.fan-out.max-concurrent-messages:10}",
    )
    fun receiveMessage(message: String) {
        val customers: List<IngestCustomerCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(
                    List::class.java,
                    IngestCustomerCommand::class.java
                )
            )

        logger.info("Received {} customers, sending them to the ingestion queue", customers.size)
        sqsProducer.sendMessages(queueUrl, customers)
    }

    @SqsListener(
        "emerion-dashboard-customer",
        maxConcurrentMessages = $$"${app.sqs.ingestion.max-concurrent-messages:20}",
    )
    fun receiveMessage(message: IngestCustomerCommand) {
        ingestionService.ingestSingle(message)
    }
}