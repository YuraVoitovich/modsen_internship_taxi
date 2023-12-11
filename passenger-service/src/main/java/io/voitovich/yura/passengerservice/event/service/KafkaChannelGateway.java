package io.voitovich.yura.passengerservice.event.service;


import io.voitovich.yura.passengerservice.event.model.ConfirmRatingReceiveModel;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaChannelGateway extends KafkaConfirmRatingReceiveHandler {

    @Override
    @Gateway(requestChannel = "confirmRatingReceiveChannel")
    void handleRatingReceiveMessage(ConfirmRatingReceiveModel model);
}
