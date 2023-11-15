package io.voitovich.yura.rideservice.dto.responce

import jakarta.persistence.Column
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class RideResponse(
    var id: UUID,
    val passengerProfileId: UUID,
    var driverProfileId: UUID,
    var startDate: LocalDateTime,
    var endDate: LocalDateTime,
    var driverRating: BigDecimal,
    var passengerRating: BigDecimal,
) {}
