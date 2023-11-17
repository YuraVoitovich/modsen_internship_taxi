package io.voitovich.yura.rideservice.dto.request

import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class GetAvailableRidesRequest(
    @NotEmpty
    val id: UUID,
    @NotEmpty
    val currentLocation: RequestPoint
)
