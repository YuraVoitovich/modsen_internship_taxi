package io.voitovich.yura.rideservice.dto.responce

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response payload for paginating and retrieving rides.")
data class RidePageResponse(
    @Schema(description = "The list of ride profiles.")
    val profiles: List<RideResponse>,

    @Schema(description = "The current page number.")
    val pageNumber: Int,

    @Schema(description = "The total number of elements.")
    val totalElements: Long,

    @Schema(description = "The total number of pages.")
    val totalPages: Int
)
