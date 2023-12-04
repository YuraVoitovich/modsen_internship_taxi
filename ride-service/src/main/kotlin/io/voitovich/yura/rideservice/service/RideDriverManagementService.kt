package io.voitovich.yura.rideservice.service

import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import java.util.*

@Validated
interface RideDriverManagementService {

    fun confirmRideStart(rideId: UUID)

    fun confirmRideEnd(rideId: UUID)

    fun updateDriverPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse

    fun getAvailableRides(getAvailableRidesRequest: GetAvailableRidesRequest) : GetAvailableRidesResponse

    fun acceptRide(acceptRideRequest: AcceptRideRequest): RideResponse

    fun ratePassenger(request: SendRatingRequest)

    fun getAllRides(driverId: UUID, @Valid request: RidePageRequest) : RidePageResponse
}