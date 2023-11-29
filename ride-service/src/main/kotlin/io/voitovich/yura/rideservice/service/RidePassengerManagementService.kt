package io.voitovich.yura.rideservice.service

import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import java.util.*

interface RidePassengerManagementService {

    fun updatePassengerPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse

    fun cancelRide(cancelRequest: CancelRequest)

    fun createRide(request: CreateRideRequest) : CreateRideResponse

    fun rateDriver(request: SendRatingRequest)

    fun getAllRides(passengerId: UUID, request: RidePageRequest) : RidePageResponse

}