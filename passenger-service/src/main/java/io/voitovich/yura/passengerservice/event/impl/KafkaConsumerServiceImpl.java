package io.voitovich.yura.passengerservice.event.impl;

import io.voitovich.yura.passengerservice.event.KafkaConsumerService;
import io.voitovich.yura.passengerservice.event.model.ReceiveRatingModel;
import io.voitovich.yura.passengerservice.service.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service

public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final String topicName = "driver_rating_topic";

    private final RatingService ratingService;

    public KafkaConsumerServiceImpl(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Override
    public void consumeRating(ReceiveRatingModel model) {
        log.info("Received message from Kafka. Processing rating for passenger with model: {}",
                model);
        ratingService.saveAndRecalculateRating(model);
    }

}
