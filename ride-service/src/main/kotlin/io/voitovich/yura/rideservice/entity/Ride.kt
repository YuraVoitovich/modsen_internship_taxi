package io.voitovich.yura.rideservice.entity

import jakarta.persistence.*
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class Ride(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,
    @Column(name = "passenger_profile_id")
    var passengerProfileId: UUID,
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: RideStatus?,
    @Column(name = "driver_profile_id")
    var driverProfileId: UUID?,
    @Column(name = "start_date")
    var startDate: LocalDateTime?,
    @Column(name = "end_date")
    var endDate: LocalDateTime?,
    @Column(name = "driver_rating")
    var driverRating: BigDecimal?,
    @Column(name = "passenger_rating")
    var passengerRating: BigDecimal?,
    @Column(name = "start_geo", columnDefinition = "geometry(Point, $SRID", nullable = false)
    var startPoint: Point,
    @Column(name = "end_geo", columnDefinition = "geometry(Point, $SRID", nullable = false)
    var endPoint: Point,
    @Column(name = "passengerPosition", columnDefinition = "geometry(Point, ${SRID}")
    var passengerPosition: Point?,
    @Column(name = "driverPosition", columnDefinition = "geometry(Point, ${SRID}")
    var driverPosition: Point?

) {
    companion object
    {
        private const val SRID = 4326
        fun builder(
            passengerProfileId: UUID,
            startPoint: Point,
            endPoint: Point
        ) = RideBuilder(passengerProfileId, startPoint, endPoint)
    }

    class RideBuilder(
        private val passengerProfileId: UUID,
        private val startPoint: Point,
        private val endPoint: Point
    ) {
        private var id: UUID? = null
        private var driverProfileId: UUID? = null
        private var startDate: LocalDateTime? = null
        private var endDate: LocalDateTime? = null
        private var driverRating: BigDecimal? = null
        private var passengerRating: BigDecimal? = null
        private var status: RideStatus? = null
        private var passengerPosition: Point? = null
        private var driverPosition: Point? = null

        fun id(id: UUID?) = apply { this.id = id }
        fun driverProfileId(driverProfileId: UUID?) = apply { this.driverProfileId = driverProfileId }
        fun startDate(startDate: LocalDateTime?) = apply { this.startDate = startDate }
        fun endDate(endDate: LocalDateTime?) = apply { this.endDate = endDate }
        fun driverRating(driverRating: BigDecimal?) = apply { this.driverRating = driverRating }
        fun passengerRating(passengerRating: BigDecimal?) = apply { this.passengerRating = passengerRating }

        fun passengerPosition(passengerPosition: Point?) = apply { this.passengerPosition = passengerPosition }
        fun driverPosition(driverPosition: Point?) = apply { this.driverPosition = driverPosition }
        fun status(status: RideStatus?) = apply { this.status = status }
        fun build() = Ride(
            id = id,
            passengerProfileId = passengerProfileId,
            driverProfileId = driverProfileId,
            startDate = startDate,
            endDate = endDate,
            driverRating = driverRating,
            passengerRating = passengerRating,
            startPoint = startPoint,
            endPoint = endPoint,
            status = status,
            passengerPosition = passengerPosition,
            driverPosition = driverPosition
        )
    }
}
