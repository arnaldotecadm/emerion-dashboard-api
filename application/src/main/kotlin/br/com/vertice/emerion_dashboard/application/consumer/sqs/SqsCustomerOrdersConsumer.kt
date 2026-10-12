package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.IngestCustomerOrdersService
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderCommand
import io.awspring.cloud.sqs.annotation.SqsListener
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

    @SqsListener("emerion-dashboard-customer-orders-batch")
    fun receiveMessage(message: String) {
        val customerOrders: List<IngestCustomerOrderCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(
                    List::class.java,
                    IngestCustomerOrderCommand::class.java
                )
            )

        println("Received ${customerOrders.size} customer orders")

        customerOrders.forEach { order ->
            println("Sending customer order with externalId=${order.externalId} to ingestion queue")
            sqsProducer.sendMessage(queueUrl, order)
        }
    }

    @SqsListener("emerion-dashboard-customer-orders")
    fun receiveMessage(message: IngestCustomerOrderCommand) {
        ingestionService.ingestSingle(message)
    }
}