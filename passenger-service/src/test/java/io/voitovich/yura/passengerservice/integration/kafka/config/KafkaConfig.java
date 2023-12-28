package io.voitovich.yura.passengerservice.integration.kafka.config;


import io.voitovich.yura.passengerservice.event.model.ConfirmRatingReceiveModel;
import io.voitovich.yura.passengerservice.event.model.ReceiveRatingModel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@ActiveProfiles("test")
@TestConfiguration
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, ReceiveRatingModel> template(KafkaProperties properties) {
        return new KafkaTemplate<>(integrationTestProducerFactory(properties));
    }

    @Bean
    public ProducerFactory<String, ReceiveRatingModel> integrationTestProducerFactory(KafkaProperties properties) {
        var configProps = properties.buildProducerProperties();
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.springframework.kafka.support.serializer.JsonSerializer");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ConsumerFactory<String, ConfirmRatingReceiveModel> integrationTestConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "passenger-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 3_000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ConfirmRatingReceiveModel.class);

        return new DefaultKafkaConsumerFactory<>(props);

    }
}
