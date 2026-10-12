package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.IngestVendedoresService
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestVendedorCommand
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
@Service
class SqsVendedorConsumer(
    private val objectMapper: ObjectMapper,
    private val sqsProducer: SQSProducer,
    private val ingestionService: IngestVendedoresService,
    @Value($$"${app.aws.queues.ingestion-requests.vendedor}") private val queueUrl: String,
) {

    @SqsListener("emerion-dashboard-vendedor-batch")
    fun receiveMessage(message: String) {
        val vendedores: List<IngestVendedorCommand> =
            objectMapper.readValue(
                message,
                objectMapper.typeFactory.constructCollectionType(
                    List::class.java,
                    IngestVendedorCommand::class.java
                )
            )

        println("Received ${vendedores.size} vendedores")

        vendedores.forEach { vendedor ->
            println("Sending vendedor with externalId=${vendedor.externalId} to ingestion queue")
            sqsProducer.sendMessage(queueUrl, vendedor)
        }
    }

    @SqsListener("emerion-dashboard-vendedor")
    fun receiveMessage(message: IngestVendedorCommand) {
        ingestionService.ingestSingle(message)
    }
}