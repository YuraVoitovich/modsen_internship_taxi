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
import java.math.RoundingMode
import java.util.*

@Component
class RideMapperImpl(
    val driverClientService: DriverClientService,
    val passengerClientService: PassengerClientService
) : RideMapper {

    private fun createRideResponseFromDriverProfileAndPassengerProfile(
        ride: Ride,
        driverProfileResponse: DriverProfileResponse,
        passengerProfileResponse: PassengerProfileResponse): RideResponse {

        return RideResponse(
            id = ride.id!!,
            passengerProfile = passengerProfileResponse,
            driverProfile = driverProfileResponse,
            startDate = ride.startDate,
            endDate = ride.endDate,
            driverRating = ride.driverRating,
            passengerRating = ride.passengerRating,
            startGeo = fromPointToResponsePoint(ride.startPoint),
            endGeo = fromPointToResponsePoint(ride.endPoint),
            passengerPosition = fromPointToResponsePoint(ride.passengerPosition),
            driverPosition = fromPointToResponsePoint(ride.driverPosition),
            status = ride.status
        )

    }
    override fun toRideResponse(ride: Ride): RideResponse {
        val passengerProfileResponse = fromPassengerProfileModelToPassengerProfileResponse(
            passengerClientService.getPassengerProfile(ride.passengerProfileId))

        val driverProfileResponse = ride.driverProfileId?.let {
            fromDriverProfileModelToDriverProfileResponse(driverClientService.getDriverProfile(it))
        }

        return createRideResponseFromDriverProfileAndPassengerProfile(ride, driverProfileResponse!!, passengerProfileResponse)
    }


    private fun getDriverProfilesMap(driverIds: List<UUID>): Map<UUID, DriverProfileResponse> {
        return driverClientService.getDriverProfiles(ids = driverIds)
            .associate { it.id to fromDriverProfileModelToDriverProfileResponse(it) }
    }

    private fun getPassengerProfilesMap(passengerIds: List<UUID>): Map<UUID, PassengerProfileResponse> {
        return passengerClientService.getPassengerProfiles(ids = passengerIds)
            .associate { it.id to fromPassengerProfileModelToPassengerProfileResponse(it) }
    }
    override fun toRideResponses(rides: List<Ride>): List<RideResponse> {
        val passengerIds = rides.map { ride: Ride -> ride.passengerProfileId }
        val passengerProfiles = getPassengerProfilesMap(passengerIds)
        val driverIds = rides.map { ride: Ride -> ride.driverProfileId!! }
        val driverProfiles = getDriverProfilesMap(driverIds)
        return rides.map{ ride: Ride ->
            createRideResponseFromDriverProfileAndPassengerProfile(
                ride,
                driverProfiles[ride.driverProfileId]!!,
                passengerProfiles[ride.passengerProfileId]!!)}
    }

    override fun toDriverRideResponses(rides: List<Ride>): List<RideResponse> {
        if (rides.isEmpty()) return listOf()
        val driverProfileResponse = rides[0].driverProfileId?.let {
            fromDriverProfileModelToDriverProfileResponse(driverClientService.getDriverProfile(it))
        }
        val passengerIds = rides.map { ride: Ride -> ride.passengerProfileId }
        val passengerProfiles = getPassengerProfilesMap(passengerIds)
        return rides.map { ride: Ride ->
            createRideResponseFromDriverProfileAndPassengerProfile(
                ride,
                driverProfileResponse!!,
                passengerProfiles[ride.passengerProfileId]!!)}
    }

    override fun toPassengerRideResponses(rides: List<Ride>): List<RideResponse> {
        if (rides.isEmpty()) return listOf()

        val passengerProfileResponse = fromPassengerProfileModelToPassengerProfileResponse(
            passengerClientService.getPassengerProfile(rides[0].passengerProfileId))

        val driverIds = rides.map { ride: Ride -> ride.driverProfileId!! }
        val driverProfileResponses = getDriverProfilesMap(driverIds)
        return rides.map { ride: Ride ->
            createRideResponseFromDriverProfileAndPassengerProfile(
                ride,
                driverProfileResponses[ride.driverProfileId]!!,
                passengerProfileResponse)}
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
            BigDecimal( model.getDistance()).setScale(0, RoundingMode.HALF_EVEN))
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