package io.voitovich.yura.rideservice.event

import io.voitovich.yura.rideservice.dto.request.SendRatingRequest

interface KafkaProducerService {

    fun ratePassenger(request: SendRatingRequest)


}