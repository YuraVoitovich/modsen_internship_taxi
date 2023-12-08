package io.voitovich.yura.rideservice.dto.mapper.impl

import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
import io.voitovich.yura.rideservice.client.service.DriverClientService
import io.voitovich.yura.rideservice.client.service.PassengerClientService
import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.request.RequestPoint
import io.voitovich.yura.rideservice.dto.responce.*
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.model.RideProjection
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class RideMapperImpl(
    val driverClientService: DriverClientService,
    val passengerClientService: PassengerClientService
) : RideMapper {
    override fun toRideResponse(ride: Ride): RideResponse {
        val driverProfileResponse = ride.driverProfileId?.let {
            fromDriverProfileModelToDriverProfileResponse(driverClientService.getDriverProfile(it))
        }
        return RideResponse(
            ride.id!!,
            fromPassengerProfileModelToPassengerProfileResponse(passengerClientService.getPassengerProfile(ride.passengerProfileId)),
            driverProfileResponse,
            ride.startDate,
            ride.endDate,
            ride.driverRating,
            ride.passengerRating,
            fromPointToResponsePoint(ride.startPoint),
            fromPointToResponsePoint(ride.endPoint),
            fromPointToResponsePoint(ride.passengerPosition),
            fromPointToResponsePoint(ride.driverPosition),
            ride.status)
    }

    private fun fromPassengerProfileModelToPassengerProfileResponse(model: PassengerProfileModel): PassengerProfileResponse {
        return PassengerProfileResponse(name = model.name, rating = model.rating)
    }

    private fun fromDriverProfileModelToDriverProfileResponse(model: DriverProfileModel): DriverProfileResponse {
        return DriverProfileResponse(name = model.name, rating = model.rating, experience = model.experience)
    }

    override fun fromCreateRequestToEntity(createRideRequest: CreateRideRequest): Ride {
        val startPoint = fromRequestPointToPoint(createRideRequest.startGeo)
        val endPoint = fromRequestPointToPoint(createRideRequest.endGeo)
        return Ride
            .builder(createRideRequest.passengerId, startPoint, endPoint, RideStatus.REQUESTED)
            .build()
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

    override fun fromPointToResponsePoint(point: Point?): ResponsePoint {
        return if (point == null) {
            ResponsePoint(0.0, 0.0);
        } else {
            ResponsePoint(point.x, point.y)
        }
    }

    companion object {
        private const val SRID = 4326;
        private val model = PrecisionModel()
        private val geometryFactory = GeometryFactory(model, SRID)
    }


}