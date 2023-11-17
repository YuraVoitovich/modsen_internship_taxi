package io.voitovich.yura.rideservice.dto.mapper

import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.request.RequestPoint
import io.voitovich.yura.rideservice.dto.responce.AvailableRideResponse
import io.voitovich.yura.rideservice.dto.responce.ResponsePoint
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.model.RideProjection
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import org.springframework.stereotype.Component
import java.math.BigDecimal

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
        val startPoint = fromRequestPointToPoint(createRideRequest.startGeo)
        val endPoint = fromRequestPointToPoint(createRideRequest.endGeo)
        return Ride.builder(createRideRequest.passengerId, startPoint, endPoint).build();
    }

    override fun fromRequestPointToPoint(requestPoint: RequestPoint): Point {
        val coords = arrayOf(Coordinate(requestPoint.latitude.toDouble(),
            requestPoint.longitude.toDouble()))
        val sequence = CoordinateArraySequence(coords)
        return Point(sequence, geometryFactory)
    }

    override fun toAvailableRideResponse(model: RideProjection): AvailableRideResponse {
        return AvailableRideResponse(model.getId(), model.getPassengerProfileId(),
            ResponsePoint(model.getStartGeo().position.getCoordinate(0),
                model.getStartGeo().position.getCoordinate(1)),
            ResponsePoint(model.getEndGeo().position.getCoordinate(0),
                model.getEndGeo().position.getCoordinate(1)),
            BigDecimal( model.getDistance()))
    }

    companion object {
        private const val SRID = 4326;
        private val model = PrecisionModel()
        private val geometryFactory = GeometryFactory(model, SRID)
    }


}