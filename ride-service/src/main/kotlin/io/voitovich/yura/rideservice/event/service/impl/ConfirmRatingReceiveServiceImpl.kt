package io.voitovich.yura.rideservice.event.service.impl

import io.voitovich.yura.rideservice.event.model.ConfirmRatingReceiveModel
import io.voitovich.yura.rideservice.event.service.ConfirmRatingReceiveService
import io.voitovich.yura.rideservice.service.RideDriverManagementService
import io.voitovich.yura.rideservice.service.RidePassengerManagementService
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class ConfirmRatingReceiveServiceImpl(
    val driverService: RideDriverManagementService,
    val passengerService: RidePassengerManagementService
) : ConfirmRatingReceiveService {

    val logger = KotlinLogging.logger {  }


    override fun handleDriverRatingReceiveConfirmation(model: ConfirmRatingReceiveModel) {
        logger.info { "handel confirm driver rating receive message with model: $model" }
        driverService.confirmDriverRated(model)
    }

    override fun handlePassengerRatingReceiveConfirmation(model: ConfirmRatingReceiveModel) {
        logger.info { "handel confirm passenger rating receive message with model: $model" }
        passengerService.confirmPassengerRated(model)
    }
}