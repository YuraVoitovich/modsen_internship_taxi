package io.voitovich.yura.rideservice.event.service

import io.voitovich.yura.rideservice.event.model.SendRatingModel
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway


@MessagingGateway
interface KafkaChannelGateway : SendRatingHandler {

    @Gateway(requestChannel = "ratePassengerChannel")
    override fun handleRatePassengerRequest(sendRatingModel: SendRatingModel)


    @Gateway(requestChannel = "rateDriverChannel")
    override fun handleRateDriverRequest(sendRatingModel: SendRatingModel)
}