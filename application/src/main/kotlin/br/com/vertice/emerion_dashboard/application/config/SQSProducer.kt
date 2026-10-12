package br.com.vertice.emerion_dashboard.application.config

import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import tools.jackson.databind.ObjectMapper

private const val MAX_BATCH_ENTRIES = 10
private const val MAX_BATCH_BYTES = 256 * 1024

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

    /**
     * Sends each message as its own SQS message using SendMessageBatch (up to 10 entries and
     * 256 KiB per request) instead of one request per message. Throws if any entry fails, so the
     * source message is redelivered; ingestion is idempotent, so re-sending is safe.
     */
    fun sendMessages(queueUrl: String, messages: List<Any>) {
        var entries = mutableListOf<SendMessageBatchRequestEntry>()
        var entriesBytes = 0

        fun flush() {
            if (entries.isEmpty()) return
            val response = sqsClient.sendMessageBatch(
                SendMessageBatchRequest.builder().queueUrl(queueUrl).entries(entries).build()
            )
            if (response.hasFailed() && response.failed().isNotEmpty()) {
                val failure = response.failed().first()
                throw IllegalStateException(
                    "Failed to enqueue ${response.failed().size} message(s) to $queueUrl: " +
                        "${failure.code()} ${failure.message()}"
                )
            }
            entries = mutableListOf()
            entriesBytes = 0
        }

        messages.forEach { message ->
            val body = objectMapper.writeValueAsString(message)
            val bodyBytes = body.toByteArray(Charsets.UTF_8).size
            if (entries.size == MAX_BATCH_ENTRIES || entriesBytes + bodyBytes > MAX_BATCH_BYTES) flush()
            entries.add(
                SendMessageBatchRequestEntry.builder().id(entries.size.toString()).messageBody(body).build()
            )
            entriesBytes += bodyBytes
        }
        flush()
    }

    fun sendMessage(topic: String, messageBody: Any) {
        val request = SendMessageRequest.builder()
            .queueUrl(topic)
            .messageBody(objectMapper.writeValueAsString(messageBody))
            .build()

        sqsClient.sendMessage(request)
    }

}