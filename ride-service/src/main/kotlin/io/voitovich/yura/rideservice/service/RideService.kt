package io.voitovich.yura.rideservice.service

import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.request.GetAvailableRidesRequest
import io.voitovich.yura.rideservice.dto.request.RidePageRequest
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import org.springframework.stereotype.Service
import java.util.UUID

interface RideService {
    fun getRideById(id: UUID) : RideResponse
    fun deleteRideById(id: UUID)
    fun getRidePage(pageRideRequest: RidePageRequest): RidePageResponse

    fun createRide(request: CreateRideRequest) : CreateRideResponse

    fun getAvailableRides(getAvailableRidesRequest: GetAvailableRidesRequest) : GetAvailableRidesResponse

}