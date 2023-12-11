package io.voitovich.yura.driverservice.event.service;

import io.voitovich.yura.driverservice.event.model.ConfirmRatingReceiveModel;

public interface KafkaConfirmRatingReceiveService {

    void confirmRatingReceive(ConfirmRatingReceiveModel model);
}
