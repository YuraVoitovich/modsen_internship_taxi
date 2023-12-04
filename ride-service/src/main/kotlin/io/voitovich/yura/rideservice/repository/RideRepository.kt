package io.voitovich.yura.rideservice.repository

import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.model.RideProjection
import org.locationtech.jts.geom.Point
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface RideRepository: JpaRepository<Ride, UUID> {
    @Query(
      value = "select id, passenger_profile_id as passengerProfileId, start_geo as startGeo, end_geo as endGeo, ST_Distance(geography(start_geo), :point) as distance from ride where status = 'REQUESTED' and st_dwithin(geography(start_geo), :point, :radius)",
      nativeQuery = true
    )
    fun getDriverAvailableRides(@Param("point") point: Point, @Param("radius") radius: Int): List<RideProjection>
    fun existsRideByPassengerProfileIdAndStatus(id: UUID, status: RideStatus) : Boolean

    fun existsRideByPassengerProfileIdAndStatusIsNotIn(passengerProfileId: UUID, statuses: Set<RideStatus>): Boolean
    @Query(
        value = "select ST_Distance(geography(driver_position), start_geo) < 30 from ride where id = :rideId",
        nativeQuery = true
    )
    fun canStartRide(rideId: UUID) : Boolean

    @Query(
        value = "select ST_Distance(geography(driver_position), end_geo) < 30 from ride where id = :rideId",
        nativeQuery = true
    )
    fun canEndRide(rideId: UUID) : Boolean

    fun getRidesByDriverProfileId(id: UUID, pageable: Pageable): Page<Ride>

    fun getRidesByPassengerProfileId(id: UUID, pageable: Pageable): Page<Ride>

}