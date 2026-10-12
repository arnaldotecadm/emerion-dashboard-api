package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.product.ingestion.IngestProductsService
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestProductCommand
import io.awspring.cloud.sqs.annotation.SqsListener
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener(
        "emerion-dashboard-products-batch",
        maxConcurrentMessages = $$"${app.sqs.fan-out.max-concurrent-messages:5}",
    )
    fun receiveMessage(message: String) {
        val products: List<IngestProductCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(
                    List::class.java,
                    IngestProductCommand::class.java
                )
            )

        logger.info("Received {} products, sending them to the ingestion queue", products.size)
        sqsProducer.sendMessages(queueUrl, products)
    }

    @SqsListener(
        "emerion-dashboard-products",
        maxConcurrentMessages = $$"${app.sqs.ingestion.max-concurrent-messages:20}",
    )
    fun receiveMessage(message: IngestProductCommand) {
        ingestProductsService.ingestSingle(message)
    }
}