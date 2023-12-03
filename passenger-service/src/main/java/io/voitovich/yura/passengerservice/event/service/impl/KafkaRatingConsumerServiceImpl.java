package io.voitovich.yura.passengerservice.event.service.impl;

import io.voitovich.yura.passengerservice.event.service.KafkaRatingConsumerService;
import io.voitovich.yura.passengerservice.event.model.ReceiveRatingModel;
import io.voitovich.yura.passengerservice.service.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaRatingConsumerServiceImpl implements KafkaRatingConsumerService {

    private final String topicName = "rate_passenger_topic";

    private final RatingService ratingService;

    public KafkaRatingConsumerServiceImpl(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Override
    public void consumeRating(ReceiveRatingModel model) {
        log.info("Received message from Kafka. Processing rating for passenger with model: {}",
                model);
        ratingService.saveAndRecalculateRating(model);
    }

}
