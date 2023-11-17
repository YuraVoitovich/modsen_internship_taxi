package io.voitovich.yura.rideservice.dto.request

import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class GetAvailableRidesRequest(
    @field:NotEmpty
    val id: UUID,
    @field:NotEmpty
    val currentLocation: RequestPoint
)
