package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.customer.ingestion.IngestCustomersService
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestCustomerCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.IngestCustomerOrdersService
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderCommand
import io.awspring.cloud.sqs.annotation.SqsListener
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

    @SqsListener("emerion-dashboard-customer-batch")
    fun receiveMessage(message: String) {
        val customers: List<IngestCustomerCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(
                    List::class.java,
                    IngestCustomerCommand::class.java
                )
            )

        println("Received ${customers.size} customers")

        customers.forEach { customer ->
            println("Sending customer with externalId=${customer.externalId} to ingestion queue")
            sqsProducer.sendMessage(queueUrl, customer)
        }
    }

    @SqsListener("emerion-dashboard-customer")
    fun receiveMessage(message: IngestCustomerCommand) {
        ingestionService.ingestSingle(message)
    }
}