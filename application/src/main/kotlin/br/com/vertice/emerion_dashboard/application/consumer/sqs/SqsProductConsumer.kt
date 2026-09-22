package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.product.ingestion.IngestProductsService
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestProductCommand
import io.awspring.cloud.sqs.annotation.SqsListener
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class SqsProductConsumer(
    private val objectMapper: ObjectMapper,
    private val sqsProducer: SQSProducer,
    private val ingestProductsService: IngestProductsService,
    @Value($$"${app.aws.queues.ingestion-requests.products}") private val queueUrl: String,
) {

    @SqsListener("emerion-dashboard-products-batch")
    fun receiveMessage(message: String) {
        val products: List<IngestProductCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(
                    List::class.java,
                    IngestProductCommand::class.java
                )
            )

        println("Received ${products.size} products")

        products.forEach { product ->
            println("Sending product with externalId=${product.externalId} to ingestion queue")
            sqsProducer.sendMessage(queueUrl, product)
        }
    }

    @SqsListener("emerion-dashboard-products")
    fun receiveMessage(message: IngestProductCommand) {
        ingestProductsService.ingestSingle(message)
    }
}