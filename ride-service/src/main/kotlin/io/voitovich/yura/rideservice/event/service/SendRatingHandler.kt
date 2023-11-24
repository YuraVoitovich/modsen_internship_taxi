package io.voitovich.yura.rideservice.event.service

import io.voitovich.yura.rideservice.event.model.SendRatingModel

interface SendRatingHandler {
    fun handleSendRatingRequest(sendRatingModel: SendRatingModel)
}