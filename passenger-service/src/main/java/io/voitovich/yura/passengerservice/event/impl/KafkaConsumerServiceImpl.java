package io.voitovich.yura.passengerservice.event.impl;

import io.voitovich.yura.passengerservice.event.KafkaConsumerService;
import io.voitovich.yura.passengerservice.event.model.RecieveRatingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final String topicName = "driver_rating_topic";

    @Override
    @KafkaListener(id = "passenger-service-group", topics = topicName)
    public void consume(RecieveRatingModel model) {
        log.info(String.format("Model: %s", model.toString()));
    }
}
