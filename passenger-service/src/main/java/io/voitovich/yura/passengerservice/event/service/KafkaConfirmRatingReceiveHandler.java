package io.voitovich.yura.passengerservice.event.service;

import io.voitovich.yura.passengerservice.event.model.ConfirmRatingReceiveModel;

public interface KafkaConfirmRatingReceiveHandler {

    void handleRatingReceiveMessage(ConfirmRatingReceiveModel model);
}
