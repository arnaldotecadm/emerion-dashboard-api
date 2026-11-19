package br.com.vertice.emerion_dashboard.application.consumer.sqs

import br.com.vertice.emerion_dashboard.application.config.SQSProducer
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.IngestLiberacaoService
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoCommand
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoItemResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome
import com.sun.net.httpserver.HttpServer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.math.BigDecimal
import java.net.InetSocketAddress
import java.net.URI
import java.security.MessageDigest
import java.time.LocalDate
import java.time.LocalTime
import java.util.Collections
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SqsLiberacaoConsumerTest {
    private val objectMapper = jacksonObjectMapper()
    private val producer = mockk<SQSProducer>()
    private val service = mockk<IngestLiberacaoService>()
    private val consumer = SqsLiberacaoConsumer(objectMapper, producer, service, "single-queue")

    @Test
    fun `deserializes a batch and forwards each release to the single queue`() {
        val first = command(100L, 1)
        val second = command(101L, 2)
        every { producer.sendMessage("single-queue", any()) } returns Unit

        consumer.receiveBatch(objectMapper.writeValueAsString(listOf(first, second)))

        verify(exactly = 1) { producer.sendMessage("single-queue", first) }
        verify(exactly = 1) { producer.sendMessage("single-queue", second) }
    }

    @Test
    fun `propagates fan out failures so the batch can be retried`() {
        val first = command(100L, 1)
        val second = command(101L, 2)
        every { producer.sendMessage("single-queue", first) } returns Unit
        every { producer.sendMessage("single-queue", second) } throws IllegalStateException("queue unavailable")

        assertFailsWith<IllegalStateException> {
            consumer.receiveBatch(objectMapper.writeValueAsString(listOf(first, second)))
        }
        verify(exactly = 1) { producer.sendMessage("single-queue", first) }
        verify(exactly = 1) { producer.sendMessage("single-queue", second) }
    }

    @Test
    fun `processes a single release and retries failures`() {
        val item = command(100L, 1)
        every { service.ingestSingle(item) } returns
            IngestLiberacaoItemResult("100:1", IngestOutcome.CREATED, null) andThen
            IngestLiberacaoItemResult("100:1", IngestOutcome.FAILED, "database unavailable")

        consumer.receiveSingle(item)
        assertFailsWith<IllegalStateException> { consumer.receiveSingle(item) }
        verify(exactly = 2) { service.ingestSingle(item) }
    }

    @Test
    fun `sends a batch and its individual records through the SQS client`() {
        val requests = Collections.synchronizedList(mutableListOf<Pair<String, String>>())
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            val payload = objectMapper.readTree(exchange.requestBody.readBytes())
            val body = payload.path("MessageBody").asString()
            requests.add(payload.path("QueueUrl").asString() to body)
            val md5 = MessageDigest.getInstance("MD5").digest(body.toByteArray()).joinToString("") { "%02x".format(it) }
            val response = """{"MessageId":"local-test","MD5OfMessageBody":"$md5"}""".toByteArray()
            exchange.responseHeaders.add("Content-Type", "application/x-amz-json-1.0")
            exchange.sendResponseHeaders(200, response.size.toLong())
            exchange.responseBody.use { it.write(response) }
        }
        server.start()
        try {
            val endpoint = "http://127.0.0.1:${server.address.port}"
            SqsClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                .build().use { client ->
                    val realProducer = SQSProducer(client, objectMapper)
                    val batchService = IngestLiberacaoService(mockk(), realProducer, "$endpoint/batch")
                    val batchConsumer = SqsLiberacaoConsumer(objectMapper, realProducer, service, "$endpoint/single")
                    val items = listOf(command(100L, 1), command(101L, 2))

                    batchService.ingest("batch-1", items)
                    assertEquals("$endpoint/batch", requests.single().first)
                    batchConsumer.receiveBatch(requests.single().second)

                    assertEquals(listOf("$endpoint/batch", "$endpoint/single", "$endpoint/single"), requests.map { it.first })
                    assertEquals(items, requests.drop(1).map { objectMapper.readValue(it.second, IngestLiberacaoCommand::class.java) })
                }
        } finally {
            server.stop(0)
        }
    }

    private fun command(numeroPedido: Long, numeroLiberacao: Int) = IngestLiberacaoCommand(
        cnpjEmpresa = "test",
        codigoEmpresa = 1,
        dataPedido = LocalDate.parse("2026-08-26"),
        dataFaturamento = LocalDate.parse("2026-08-26"),
        numeroPedido = numeroPedido,
        numeroLiberacao = numeroLiberacao,
        dataLiberacao = LocalDate.parse("2026-08-27"),
        horaLiberacao = LocalTime.parse("08:50:13"),
        codigoCliente = 2,
        quantidadeSeparada = BigDecimal.ONE,
        totalLiberadoSemImpostos = BigDecimal.ONE,
        totalLiberadoComImpostos = BigDecimal.ONE,
        situacaoLiberacao = "open",
        codigoVendedor = 3,
        comissaoLiberacao = BigDecimal.ZERO,
        totalCusto = BigDecimal.ONE,
        detalhes = emptyList(),
    )
}
