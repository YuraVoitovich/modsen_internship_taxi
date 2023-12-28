package io.voitovich.yura.rideservice.integration.kafka.config

import io.voitovich.yura.rideservice.event.model.ConfirmRatingReceiveModel
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@TestConfiguration
class KafkaConsumerTestConfig {

    @Bean
    fun template(properties: KafkaProperties): KafkaTemplate<String, ConfirmRatingReceiveModel> {
        return KafkaTemplate(
            integrationTestProducerFactory(properties)
        )
    }

    @Bean
    fun integrationTestProducerFactory(properties: KafkaProperties): ProducerFactory<String, ConfirmRatingReceiveModel> {
        val configProps = properties.buildProducerProperties()
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            "org.apache.kafka.common.serialization.StringSerializer"
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
            "org.springframework.kafka.support.serializer.JsonSerializer"
        return DefaultKafkaProducerFactory(
            configProps
        )
    }
}