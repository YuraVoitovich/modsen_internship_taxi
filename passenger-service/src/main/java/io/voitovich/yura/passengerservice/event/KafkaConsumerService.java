package io.voitovich.yura.passengerservice.event;

import io.voitovich.yura.passengerservice.event.model.ReceiveRatingModel;

public interface KafkaConsumerService {

    void consume(ReceiveRatingModel model);
}
