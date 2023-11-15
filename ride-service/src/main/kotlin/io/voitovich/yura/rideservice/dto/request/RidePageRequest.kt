package io.voitovich.yura.rideservice.dto.request

import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.validation.annotations.OrderBy
import jakarta.validation.constraints.Min

data class RidePageRequest(
    @Min(1)
    val pageNumber: Int,
    @Min(1)
    val pageSize: Int,
    @OrderBy(RideResponse::class)
    val orderBy: String,

    ) {

}
