package io.voitovich.yura.rideservice.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.validation.annotations.OrderBy
import jakarta.validation.constraints.Min

@Schema(description = "Request payload for paginating and ordering rides.")
data class RidePageRequest(
    @field:Min(1, message = "Page number must be greater, then 0")
    @Schema(description = "The page number.", minimum = "1")
    val pageNumber: Int,

    @field:Min(1, message = "Page size must be greater, then 0")
    @Schema(description = "The page size.", minimum = "1")
    val pageSize: Int,

    @field:OrderBy(RideResponse::class)
    @Schema(description = "The field to order the rides by.")
    val orderBy: String
)