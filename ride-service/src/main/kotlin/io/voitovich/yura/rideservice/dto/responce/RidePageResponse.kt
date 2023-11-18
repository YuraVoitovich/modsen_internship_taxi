package io.voitovich.yura.rideservice.dto.responce

data class RidePageResponse(
    val profiles: List<RideResponse>,
    val pageNumber: Int,
    val totalElements: Long,
    val totalPages: Int,
) {

}
