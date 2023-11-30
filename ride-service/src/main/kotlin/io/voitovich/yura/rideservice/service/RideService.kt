package io.voitovich.yura.rideservice.service

import io.voitovich.yura.rideservice.dto.request.RidePageRequest
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import java.util.*

@Validated
interface RideService {
    fun getRideById(id: UUID) : RideResponse
    fun deleteRideById(id: UUID)
    fun getRidePage(@Valid pageRideRequest: RidePageRequest): RidePageResponse

}