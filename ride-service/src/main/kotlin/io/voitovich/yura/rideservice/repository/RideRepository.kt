package io.voitovich.yura.rideservice.repository

import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.model.RideProjection
import org.locationtech.jts.geom.Point
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

interface RideRepository: JpaRepository<Ride, UUID> {
    @Query(
      value = "select id, passenger_profile_id as passengerProfileId, start_geo as startGeo, end_geo as endGeo, ST_Distance(geography(start_geo), end_geo) as distance from ride where status = 'ACCEPTED' and st_dwithin(geography(start_geo), :point, :radius)",
      nativeQuery = true
    )
    fun getDriverAvailableRides(@Param("point") point: Point, @Param("radius") radius: Int): List<RideProjection>
    fun existsRideByPassengerProfileIdAndStatus(id: UUID, status: RideStatus) : Boolean

}