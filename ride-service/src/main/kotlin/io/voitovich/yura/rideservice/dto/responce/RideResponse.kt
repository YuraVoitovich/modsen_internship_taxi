package io.voitovich.yura.rideservice.dto.responce

import io.swagger.v3.oas.annotations.media.Schema
import io.voitovich.yura.rideservice.entity.RideStatus
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Schema(description = "Response payload for a ride.")
data class RideResponse(
    @Schema(description = "The UUID of the ride.")
    var id: UUID,

    @Schema(description = "The UUID of the passenger profile.")
    val passengerProfileId: UUID,

    @Schema(description = "The UUID of the driver profile.")
    var driverProfileId: UUID?,

    @Schema(description = "The start date of the ride.")
    var startDate: LocalDateTime?,

    @Schema(description = "The end date of the ride.")
    var endDate: LocalDateTime?,

    @Schema(description = "The rating given by the driver.")
    var driverRating: BigDecimal?,

    @Schema(description = "The rating given by the passenger.")
    var passengerRating: BigDecimal?,

    @Schema(description = "The starting geographic location of the ride.")
    var startGeo: ResponsePoint?,

    @Schema(description = "The ending geographic location of the ride.")
    var endGeo: ResponsePoint?,

    @Schema(description = "The current position of the passenger.")
    var passengerPosition: ResponsePoint?,

    @Schema(description = "The current position of the driver.")
    var driverPosition: ResponsePoint?,

    @Schema(description = "The status of the ride.")
    var status: RideStatus?
)
