package io.voitovich.yura.driverservice.event.service;

import io.voitovich.yura.driverservice.event.model.ReceiveRatingModel;

public interface KafkaRatingConsumerService {

    void consumeRating(ReceiveRatingModel model);
}
