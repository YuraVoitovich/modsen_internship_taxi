package io.voitovich.yura.rideservice.dto.request

import jakarta.validation.constraints.NotNull
import java.util.*

data class CreateRideRequest(
    @field:NotNull
    val passengerId: UUID,
    @field:NotNull
    val startGeo: RequestPoint,
    @field:NotNull
    val endGeo: RequestPoint,
)
