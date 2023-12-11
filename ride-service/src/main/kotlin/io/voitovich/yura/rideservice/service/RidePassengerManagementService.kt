package io.voitovich.yura.rideservice.service

import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.event.model.ConfirmRatingReceiveModel
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import java.util.*

@Validated
interface RidePassengerManagementService {

    fun updatePassengerPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse

    fun cancelRide(cancelRequest: CancelRequest)

    fun createRide(request: CreateRideRequest) : CreateRideResponse

    fun rateDriver(request: SendRatingRequest)

    fun confirmPassengerRated(model: ConfirmRatingReceiveModel)

    fun getAllRides(passengerId: UUID, @Valid request: RidePageRequest) : RidePageResponse

}