package io.voitovich.yura.rideservice.config

import io.voitovich.yura.rideservice.event.model.ConfirmRatingReceiveModel
import io.voitovich.yura.rideservice.event.service.ConfirmRatingReceiveService
import io.voitovich.yura.rideservice.properties.DefaultKafkaProperties
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.kafka.dsl.Kafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.messaging.support.ChannelInterceptor


@Configuration
class KafkaConsumerConfig(
    val kafkaProperties: DefaultKafkaProperties,
    val confirmRatingReceiveService: ConfirmRatingReceiveService,
    val channelInterceptor: ChannelInterceptor
) {

    @Bean
    fun handleDriverRatingReceiveConfirmation(consumerFactory: ConsumerFactory<String, ConfirmRatingReceiveModel>): IntegrationFlow {
        return IntegrationFlow.from(
            Kafka.messageDrivenChannelAdapter(
                consumerFactory,
                kafkaProperties.confirmDriverRatingReceiveTopicName
            )
        )
            .intercept(channelInterceptor)
            .handle(confirmRatingReceiveService, "handleDriverRatingReceiveConfirmation")
            .get()
    }

    @Bean
    fun handlePassengerRatingReceiveConfirmation(consumerFactory: ConsumerFactory<String, ConfirmRatingReceiveModel>): IntegrationFlow {
        return IntegrationFlow.from(
            Kafka.messageDrivenChannelAdapter(
                consumerFactory,
                kafkaProperties.confirmPassengerRatingReceiveTopicName
            )
        )
            .intercept(channelInterceptor)
            .handle(confirmRatingReceiveService, "handlePassengerRatingReceiveConfirmation")
            .get()
    }


    @Bean
    fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, ConfirmRatingReceiveModel> {
        val props = kafkaProperties.buildConsumerProperties()
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        props[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG] = 3000
        props[JsonDeserializer.TRUSTED_PACKAGES] = "*"
        props[JsonDeserializer.USE_TYPE_INFO_HEADERS] = false
        props[JsonDeserializer.VALUE_DEFAULT_TYPE] = ConfirmRatingReceiveModel::class.java
        return DefaultKafkaConsumerFactory(props)
    }


    @Bean
    fun kafkaListenerContainerFactory(
        kafkaProperties: KafkaProperties
    ): ConcurrentKafkaListenerContainerFactory<String, ConfirmRatingReceiveModel> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, ConfirmRatingReceiveModel> =
            ConcurrentKafkaListenerContainerFactory<String, ConfirmRatingReceiveModel>()
        factory.consumerFactory = consumerFactory(kafkaProperties)
        return factory
    }
}