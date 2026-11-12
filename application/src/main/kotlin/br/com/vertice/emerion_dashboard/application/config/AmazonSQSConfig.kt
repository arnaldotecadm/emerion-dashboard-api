package br.com.vertice.emerion_dashboard.application.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
class AmazonSQSConfig(
    @Value("\${app.aws.region}") private val region: String,
    @Value("\${app.aws.aws-profile}") private val awsProfile: String,
) {
    @Bean
    fun sqsClient(): SqsClient {
        return SqsClient.builder()
            .region(Region.of(region))
            .credentialsProvider(
                ProfileCredentialsProvider.create(awsProfile)
            )
            .build()
    }

    @Bean
    fun sqsAsyncClient(): SqsAsyncClient {
        return SqsAsyncClient.builder()
            .region(Region.of(region))
            .credentialsProvider(
                ProfileCredentialsProvider.create(awsProfile)
            )
            .build()
    }
}