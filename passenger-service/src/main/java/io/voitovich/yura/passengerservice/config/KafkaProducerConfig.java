package io.voitovich.yura.passengerservice.config;

import io.voitovich.yura.passengerservice.event.model.ConfirmRatingReceiveModel;
import io.voitovich.yura.passengerservice.properties.DefaultKafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.MessageChannel;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final DefaultKafkaProperties kafkaProperties;


    @Bean
    public IntegrationFlow sendToKafkaFlow(KafkaProperties properties) {
        return f -> f.channel("confirmRatingReceiveChannel")
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate(properties))
                        .messageKey(m -> m.getHeaders().get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER))
                        .topic(kafkaProperties.getConfirmRatingReceiveTopicName()));
    }

    @Bean
    public ProducerFactory<String, ConfirmRatingReceiveModel> producerFactory(KafkaProperties properties) {
        var configProps = properties.buildProducerProperties();
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.springframework.kafka.support.serializer.JsonSerializer");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ConfirmRatingReceiveModel> kafkaTemplate(KafkaProperties properties) {
        var template = new KafkaTemplate<>(producerFactory(properties));
        template.setObservationEnabled(true);
        return template;
    }

    @Bean
    public MessageChannel confirmRatingReceiveChannel() {
        return new DirectChannel();
    }
}
