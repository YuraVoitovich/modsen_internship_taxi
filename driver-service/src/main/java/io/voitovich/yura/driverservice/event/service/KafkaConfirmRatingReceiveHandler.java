package io.voitovich.yura.driverservice.event.service;

import io.voitovich.yura.driverservice.event.model.ConfirmRatingReceiveModel;

public interface KafkaConfirmRatingReceiveHandler {

    void handleRatingReceiveMessage(ConfirmRatingReceiveModel model);
}
