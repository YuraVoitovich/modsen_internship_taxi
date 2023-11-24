package io.voitovich.yura.rideservice.event.service

import io.voitovich.yura.rideservice.event.model.SendRatingModel
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway


@MessagingGateway(defaultRequestChannel = "sendToKafkaChannel")
interface KafkaChannelGateway : SendRatingHandler {

    @Gateway(requestChannel = "sendToKafkaChannel")
    override fun handleSendRatingRequest(sendRatingModel: SendRatingModel);
}