package io.voitovich.yura.driverservice.event.service;


import io.voitovich.yura.driverservice.event.model.ConfirmRatingReceiveModel;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaChannelGateway {

    @Gateway(requestChannel = "confirmRatingReceiveChannel")
    void handleRatingReceiveMessage(ConfirmRatingReceiveModel model);
}
