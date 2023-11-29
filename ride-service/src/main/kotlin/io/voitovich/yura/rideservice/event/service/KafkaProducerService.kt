package io.voitovich.yura.rideservice.event.service

import io.voitovich.yura.rideservice.event.model.SendRatingModel

interface KafkaProducerService {

    fun ratePassenger(model: SendRatingModel)

    fun rateDriver(model: SendRatingModel)
}