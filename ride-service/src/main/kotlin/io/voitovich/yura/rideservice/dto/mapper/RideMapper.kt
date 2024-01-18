package io.voitovich.yura.rideservice.dto.mapper

import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.request.RequestPoint
import io.voitovich.yura.rideservice.dto.responce.AvailableRideResponse
import io.voitovich.yura.rideservice.dto.responce.ResponsePoint
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.model.RideProjection
import org.locationtech.jts.geom.Point


interface RideMapper {


    fun toDriverRideResponses(rides: List<Ride>): List<RideResponse>

    fun toPassengerRideResponses(rides: List<Ride>): List<RideResponse>
    fun toRideResponse(ride: Ride): RideResponse

    fun toRideResponses(rides: List<Ride>): List<RideResponse>

    fun fromCreateRequestToEntity(createRideRequest: CreateRideRequest) : Ride

    fun fromRequestPointToPoint(requestPoint: RequestPoint) : Point

    fun toAvailableRideResponse(model: RideProjection) : AvailableRideResponse

    fun fromPointToResponsePoint(point: Point?) : ResponsePoint
}