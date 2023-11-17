package io.voitovich.yura.rideservice.dto.responce

data class GetAvailableRidesResponse(
    val rides: List<AvailableRideResponse>,
)
