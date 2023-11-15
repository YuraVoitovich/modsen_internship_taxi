package io.voitovich.yura.rideservice.dto.responce

import io.voitovich.yura.rideservice.entity.Ride
import kotlin.reflect.KFunction1

data class RidePageResponse(
    val profiles: List<RideResponse>,
    val pageNumber: Int,
    val totalElements: Long,
    val totalPages: Int,
) {

}
