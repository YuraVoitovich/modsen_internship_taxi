package io.voitovich.yura.rideservice.dto.responce

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Represents a geographic point in a response.")
data class ResponsePoint(
    @Schema(description = "The latitude of the point.")
    val latitude: Double,

    @Schema(description = "The longitude of the point.")
    val longitude: Double
)
