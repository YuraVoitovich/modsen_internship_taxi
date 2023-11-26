package io.voitovich.yura.driverservice.event.service;

import io.voitovich.yura.driverservice.event.model.ReceiveRatingModel;

public interface KafkaConsumerService {

    void consumeRating(ReceiveRatingModel model);
}
