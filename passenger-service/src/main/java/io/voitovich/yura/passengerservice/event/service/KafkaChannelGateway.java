package io.voitovich.yura.passengerservice.event.service;


import io.voitovich.yura.passengerservice.event.model.ConfirmRatingReceiveModel;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaChannelGateway {

    @Gateway(requestChannel = "confirmRatingReceiveChannel")
    void handleRatingReceiveMessage(ConfirmRatingReceiveModel model);
}
