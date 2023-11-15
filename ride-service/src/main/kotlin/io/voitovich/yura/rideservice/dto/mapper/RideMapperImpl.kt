package io.voitovich.yura.rideservice.dto.mapper

import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.entity.Ride
import org.springframework.stereotype.Component

@Component
class RideMapperImpl : RideMapper {
    override fun toRideResponse(ride: Ride): RideResponse {
        return RideResponse(
            ride.id,
            ride.passengerProfileId,
            ride.driverProfileId,
            ride.startDate,
            ride.endDate,
            ride.driverRating,
            ride.passengerRating)
    }

    override fun toDto(rideResponse: RideResponse): Ride {
        return Ride(
            rideResponse.id,
            rideResponse.passengerProfileId,
            rideResponse.driverProfileId,
            rideResponse.startDate,
            rideResponse.endDate,
            rideResponse.driverRating,
            rideResponse.passengerRating)
    }
}