package io.voitovich.yura.passengerservice.event.service;


import io.voitovich.yura.passengerservice.event.model.ConfirmRatingReceiveModel;

public interface KafkaConfirmRatingReceiveService {

    void confirmRatingReceive(ConfirmRatingReceiveModel model);
}
