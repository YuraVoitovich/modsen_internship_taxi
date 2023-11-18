package io.voitovich.yura.rideservice.service

import io.voitovich.yura.rideservice.dto.request.AcceptRideRequest
import io.voitovich.yura.rideservice.dto.request.GetAvailableRidesRequest
import io.voitovich.yura.rideservice.dto.request.UpdatePositionRequest
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import java.util.UUID

interface RideDriverManagementService {

    fun confirmRideStart(rideId: UUID)

    fun updateDriverPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse

    fun getAvailableRides(getAvailableRidesRequest: GetAvailableRidesRequest) : GetAvailableRidesResponse

    fun acceptRide(acceptRideRequest: AcceptRideRequest): RideResponse
}