package io.voitovich.yura.passengerservice.event;

import io.voitovich.yura.passengerservice.event.model.RecieveRatingModel;

public interface KafkaConsumerService {

    void consume(RecieveRatingModel model);
}
