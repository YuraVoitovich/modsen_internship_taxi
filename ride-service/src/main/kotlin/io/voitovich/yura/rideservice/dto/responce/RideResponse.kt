package io.voitovich.yura.rideservice.dto.responce

import io.voitovich.yura.rideservice.entity.RideStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class RideResponse(
    var id: UUID,
    val passengerProfileId: UUID,
    var driverProfileId: UUID?,
    var startDate: LocalDateTime?,
    var endDate: LocalDateTime?,
    var driverRating: BigDecimal?,
    var passengerRating: BigDecimal?,
    var startGeo: ResponsePoint?,
    var endGeo: ResponsePoint?,
    var passengerPosition: ResponsePoint?,
    var driverPosition: ResponsePoint?,
    var status: RideStatus?
) {}
