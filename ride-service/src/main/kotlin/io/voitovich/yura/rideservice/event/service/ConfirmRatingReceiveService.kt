package io.voitovich.yura.rideservice.event.service

import io.voitovich.yura.rideservice.event.model.ConfirmRatingReceiveModel

interface ConfirmRatingReceiveService {

    fun handleDriverRatingReceiveConfirmation(model: ConfirmRatingReceiveModel)

    fun handlePassengerRatingReceiveConfirmation(model: ConfirmRatingReceiveModel)

}