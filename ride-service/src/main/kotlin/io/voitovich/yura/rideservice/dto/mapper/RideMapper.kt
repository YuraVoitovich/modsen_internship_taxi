package io.voitovich.yura.rideservice.dto.mapper

import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.entity.Ride


interface RideMapper {
    fun toRideResponse(ride: Ride): RideResponse

    fun fromCreateRequestToEntity(createRideRequest: CreateRideRequest) : Ride
}