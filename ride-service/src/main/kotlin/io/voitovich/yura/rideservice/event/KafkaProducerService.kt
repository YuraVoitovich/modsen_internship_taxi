package io.voitovich.yura.rideservice.event

import io.voitovich.yura.rideservice.event.model.SendRatingModel

interface KafkaProducerService {

    fun sendRating(model: SendRatingModel)
}