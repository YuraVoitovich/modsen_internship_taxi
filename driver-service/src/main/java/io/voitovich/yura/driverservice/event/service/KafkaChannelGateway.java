package io.voitovich.yura.driverservice.event.service;


import io.voitovich.yura.driverservice.event.model.ConfirmRatingReceiveModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaChannelGateway extends KafkaConfirmRatingReceiveHandler {

    @Override
    @Gateway(requestChannel = "confirmRatingReceiveChannel")
    void handleRatingReceiveMessage(ConfirmRatingReceiveModel model);
}
