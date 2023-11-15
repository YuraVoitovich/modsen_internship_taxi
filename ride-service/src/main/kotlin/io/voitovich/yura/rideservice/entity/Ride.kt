package io.voitovich.yura.rideservice.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class Ride(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID,
    @Column(name = "passenger_profile_id")
    var passengerProfileId: UUID,
    @Column(name = "driver_profile_id")
    var driverProfileId: UUID,
    @Column(name = "start_date")
    var startDate: LocalDateTime,
    @Column(name = "end_date")
    var endDate: LocalDateTime,
    @Column(name = "driver_rating")
    var driverRating: BigDecimal,
    @Column(name = "passenger_rating")
    var passengerRating: BigDecimal,
)
