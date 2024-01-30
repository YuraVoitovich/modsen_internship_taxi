package io.voitovich.yura.driverservice.config;

import io.voitovich.yura.driverservice.event.model.ReceiveRatingModel;
import io.voitovich.yura.driverservice.event.service.KafkaRatingConsumerService;
import io.voitovich.yura.driverservice.properties.DefaultKafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final DefaultKafkaProperties kafkaProperties;
    private final KafkaRatingConsumerService service;
    private final ChannelInterceptor channelInterceptor;
    @Bean
    public IntegrationFlow consumeFromKafka(ConsumerFactory<String, ReceiveRatingModel> consumerFactory) {
        return IntegrationFlow.from(Kafka
                        .messageDrivenChannelAdapter(consumerFactory, kafkaProperties.getConsumeRatingTopicName()))
                .intercept(channelInterceptor)
                .handle(service, "consumeRating")
                .get();
    }
    @Bean
    public ConsumerFactory<String, ReceiveRatingModel> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 3_000);

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ReceiveRatingModel.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReceiveRatingModel> kafkaListenerContainerFactory(
            KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, ReceiveRatingModel> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaProperties));
        return factory;
    }
}
