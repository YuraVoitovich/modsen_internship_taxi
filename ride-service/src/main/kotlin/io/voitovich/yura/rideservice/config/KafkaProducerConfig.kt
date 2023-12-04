package io.voitovich.yura.rideservice.config

import io.voitovich.yura.rideservice.event.model.SendRatingModel
import io.voitovich.yura.rideservice.properties.DefaultKafkaProperties
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.IntegrationMessageHeaderAccessor
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlowDefinition
import org.springframework.integration.kafka.dsl.Kafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.messaging.MessageChannel


@Configuration
class KafkaProducerConfig(private val defaultKafkaProperties: DefaultKafkaProperties) {

    private var ratePassengerChannelName = "ratePassengerChannel"
    private var rateDriverChannelName = "rateDriverChannel"

    @Bean
    fun ratePassengerFlow(properties: KafkaProperties): IntegrationFlow {
        return IntegrationFlow {
            f: IntegrationFlowDefinition<*> ->
            f.channel(ratePassengerChannelName)
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate(properties))
                    .messageKey<Any?> { m -> m.headers[IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER] }
                    .topic(defaultKafkaProperties.sendRatingToPassengerTopicName))
        }
    }

    @Bean
    fun rateDriverFlow(properties: KafkaProperties): IntegrationFlow {
        return IntegrationFlow {
                f: IntegrationFlowDefinition<*> ->
            f.channel(rateDriverChannelName)
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate(properties))
                    .messageKey<Any?> { m -> m.headers[IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER] }
                    .topic(defaultKafkaProperties.sendRatingToDriverTopicName))
        }
    }
    @Bean
    fun producerFactory(properties: KafkaProperties): ProducerFactory<String, SendRatingModel> {
        val configProps = properties.buildProducerProperties()
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = "org.apache.kafka.common.serialization.StringSerializer"
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = "org.springframework.kafka.support.serializer.JsonSerializer"
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(properties: KafkaProperties): KafkaTemplate<String, SendRatingModel> {
        return KafkaTemplate(producerFactory(properties))
    }

    @Bean
    fun sendToKafkaChannel(): MessageChannel {
        return DirectChannel()
    }

    @Bean
    fun ratePassengerTopic(): NewTopic{
        return NewTopic(defaultKafkaProperties.sendRatingToPassengerTopicName,
            defaultKafkaProperties.sendRatingToPassengerTopicNumPartitions,
            defaultKafkaProperties.sendRatingToPassengerTopicReplicasAssignments)
    }

    @Bean
    fun rateDriverTopic(): NewTopic{
        return NewTopic(defaultKafkaProperties.sendRatingToDriverTopicName,
            defaultKafkaProperties.sendRatingToDriverTopicNumPartitions,
            defaultKafkaProperties.sendRatingToDriverTopicReplicasAssignments)
    }
}