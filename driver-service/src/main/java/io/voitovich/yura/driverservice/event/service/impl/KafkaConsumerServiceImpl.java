package io.voitovich.yura.driverservice.event.service.impl;

import io.voitovich.yura.driverservice.event.model.ReceiveRatingModel;
import io.voitovich.yura.driverservice.event.service.KafkaConsumerService;
import io.voitovich.yura.driverservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final RatingService ratingService;
    @Override
    public void consumeRating(ReceiveRatingModel model) {
        log.info("Received message from Kafka. Processing rating for driver with model: {}",
                model);
        ratingService.saveAndRecalculateRating(model);
    }
}
