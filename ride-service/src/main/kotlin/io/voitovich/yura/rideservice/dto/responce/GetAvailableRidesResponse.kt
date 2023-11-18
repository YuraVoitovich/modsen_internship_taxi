package io.voitovich.yura.rideservice.dto.responce

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response payload for retrieving available rides.")
data class GetAvailableRidesResponse(
    @Schema(description = "The list of available rides.")
    val rides: List<AvailableRideResponse>
)