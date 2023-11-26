package io.voitovich.yura.rideservice.event.service

import io.voitovich.yura.rideservice.dto.request.SendRatingRequest

interface KafkaProducerService {

    fun ratePassenger(request: SendRatingRequest)

    fun rateDriver(request: SendRatingRequest)
}