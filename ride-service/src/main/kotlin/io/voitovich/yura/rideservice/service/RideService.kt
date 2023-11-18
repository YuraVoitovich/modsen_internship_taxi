package io.voitovich.yura.rideservice.service

import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.*
import java.util.*

interface RideService {
    fun getRideById(id: UUID) : RideResponse
    fun deleteRideById(id: UUID)
    fun getRidePage(pageRideRequest: RidePageRequest): RidePageResponse

}