package br.com.vertice.emerion_dashboard.application.config

import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import tools.jackson.databind.ObjectMapper

@Service
class SQSProducer(
    private val sqsClient: SqsClient,
    private val objectMapper: ObjectMapper
) {

    fun sendMessageBatch(topic: String, messageBody: List<Any>) {
        val request = SendMessageRequest.builder()
            .queueUrl(topic)
            .messageBody(objectMapper.writeValueAsString(messageBody))
            .build()

        sqsClient.sendMessage(request)
    }

    fun sendMessage(topic: String, messageBody: Any) {
        val request = SendMessageRequest.builder()
            .queueUrl(topic)
            .messageBody(objectMapper.writeValueAsString(messageBody))
            .build()

        sqsClient.sendMessage(request)
    }

}