package io.voitovich.yura.rideservice.dto.mapper

import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.entity.Ride
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RideMapperImpl : RideMapper {
    override fun toRideResponse(ride: Ride): RideResponse {
        return RideResponse(
            ride.id!!,
            ride.passengerProfileId,
            ride.driverProfileId,
            ride.startDate,
            ride.endDate,
            ride.driverRating,
            ride.passengerRating)
    }

    override fun fromCreateRequestToEntity(createRideRequest: CreateRideRequest): Ride {
        var startCoords = arrayOf(Coordinate(createRideRequest.startGeo.latitude.toDouble(),
            createRideRequest.startGeo.longitude.toDouble()));
        var startSequence = CoordinateArraySequence(startCoords);
        var endCoords = arrayOf(Coordinate(createRideRequest.endGeo.latitude.toDouble(),
            createRideRequest.endGeo.longitude.toDouble()));
        var endSequence = CoordinateArraySequence(endCoords);
        return Ride(
            null,
            createRideRequest.passengerId,
            null,
            null,
            null,
            null,
            null,
            Point(startSequence, geometryFactory),
            Point(endSequence, geometryFactory),
        )
    }

    companion object {
        private const val SRID = 4326;
        private val model = PrecisionModel()
        private val geometryFactory = GeometryFactory(model, SRID)
    }


}