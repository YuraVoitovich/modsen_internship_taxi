package io.voitovich.yura.rideservice.service

import io.voitovich.yura.rideservice.dto.request.CancelRequest
import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.request.SendRatingRequest
import io.voitovich.yura.rideservice.dto.request.UpdatePositionRequest
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse

interface RidePassengerManagementService {

    fun updatePassengerPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse

    fun cancelRide(cancelRequest: CancelRequest)

    fun createRide(request: CreateRideRequest) : CreateRideResponse

    fun rateDriver(request: SendRatingRequest)

}