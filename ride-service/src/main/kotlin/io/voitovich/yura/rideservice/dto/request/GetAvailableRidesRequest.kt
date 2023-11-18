package io.voitovich.yura.rideservice.dto.request

import jakarta.validation.constraints.NotNull
import java.util.*

data class GetAvailableRidesRequest(
    @field:NotNull
    val id: UUID,
    @field:NotNull
    val currentLocation: RequestPoint,
    val radius: Int?
)
