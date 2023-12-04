package io.voitovich.yura.passengerservice.event.service;

import io.voitovich.yura.passengerservice.event.model.ReceiveRatingModel;

public interface KafkaRatingConsumerService {

    void consumeRating(ReceiveRatingModel model);
}
