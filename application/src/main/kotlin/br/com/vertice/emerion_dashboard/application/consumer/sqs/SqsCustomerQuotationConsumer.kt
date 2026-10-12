package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.customerquotation.ingestion.IngestCustomerQuotationService
import br.com.vertice.emerion_dashboard.application.customerquotation.ingestion.model.IngestCustomerQuotationCommand
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class SqsCustomerQuotationConsumer(
    private val objectMapper: ObjectMapper,
    private val sqsProducer: SQSProducer,
    private val ingestCustomerQuotationService: IngestCustomerQuotationService,
    @Value($$"${app.aws.queues.ingestion-requests.customer-quotation}") private val queueUrl: String,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener("emerion-dashboard-customer-quotation-batch")
    fun receiveMessage(message: String) {
        val quotations: List<IngestCustomerQuotationCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(
                    List::class.java,
                    IngestCustomerQuotationCommand::class.java
                )
            )

        logger.info("Received {} customer quotations, sending them to the ingestion queue", quotations.size)
        quotations.forEach { sqsProducer.sendMessage(queueUrl, it) }
    }

    @SqsListener(
        "emerion-dashboard-customer-quotation",
        maxConcurrentMessages = "3",
        maxMessagesPerPoll = "3",
    )
    fun receiveMessage(message: IngestCustomerQuotationCommand) {
        ingestCustomerQuotationService.ingestSingle(message)
    }
}
