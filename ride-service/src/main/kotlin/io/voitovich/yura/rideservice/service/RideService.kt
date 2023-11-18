package io.voitovich.yura.rideservice.service

import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.*
import java.util.*

interface RideService {
    fun getRideById(id: UUID) : RideResponse
    fun deleteRideById(id: UUID)
    fun getRidePage(pageRideRequest: RidePageRequest): RidePageResponse

    fun createRide(request: CreateRideRequest) : CreateRideResponse

    fun getAvailableRides(getAvailableRidesRequest: GetAvailableRidesRequest) : GetAvailableRidesResponse

    fun acceptRide(acceptRideRequest: AcceptRideRequest): RideResponse

    fun updateDriverPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse

    fun updatePassengerPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse

    fun cancelRide(cancelRequest: CancelRequest)
}