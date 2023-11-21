package io.voitovich.yura.rideservice.config

import io.voitovich.yura.rideservice.event.model.SendRatingModel
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.support.serializer.JsonSerializer


@Configuration
class KafkaProducerConfig {

    private var replicasAssignments : Short = 1
    private var topicName: String = "driver_rating_topic"
    private var numPartitions: Int = 1;

    @Bean
    fun producerFactory(): ProducerFactory<String, SendRatingModel> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "kafka:9092"
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = "org.apache.kafka.common.serialization.StringSerializer"
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = "org.springframework.kafka.support.serializer.JsonSerializer"
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, SendRatingModel> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun topic(): NewTopic{
        return NewTopic(topicName, numPartitions,replicasAssignments)
    }
}