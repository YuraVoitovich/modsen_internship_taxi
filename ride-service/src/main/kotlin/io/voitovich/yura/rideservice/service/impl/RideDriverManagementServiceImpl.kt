package io.voitovich.yura.rideservice.service.impl

import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.request.AcceptRideRequest
import io.voitovich.yura.rideservice.dto.request.GetAvailableRidesRequest
import io.voitovich.yura.rideservice.dto.request.UpdatePositionRequest
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.exception.NoSuchRecordException
import io.voitovich.yura.rideservice.exception.RideAlreadyAccepted
import io.voitovich.yura.rideservice.exception.RideStartConfirmationException
import io.voitovich.yura.rideservice.repository.RideRepository
import io.voitovich.yura.rideservice.service.RideDriverManagementService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class RideDriverManagementServiceImpl(val repository: RideRepository, val mapper: RideMapper) : RideDriverManagementService {

    @Value("\${default.search-radius}")
    private var DEFAULT_RADIUS : Int = 300

    override fun getAvailableRides(getAvailableRidesRequest: GetAvailableRidesRequest): GetAvailableRidesResponse {
        val rides = repository.getDriverAvailableRides(mapper
            .fromRequestPointToPoint(getAvailableRidesRequest.currentLocation),
            getAvailableRidesRequest.radius ?: DEFAULT_RADIUS)
        return GetAvailableRidesResponse(rides
            .map { t -> mapper.toAvailableRideResponse(t) }.toList())
    }

    override fun acceptRide(acceptRideRequest: AcceptRideRequest) : RideResponse {
        val rideOptional = repository.findById(acceptRideRequest.rideId)
        val ride = rideOptional.orElseThrow { NoSuchRecordException(String
            .format("Ride with id: {%s} was not found", acceptRideRequest.rideId))
        }
        if (ride.status == RideStatus.REQUESTED) {
            ride.status = RideStatus.ACCEPTED
            ride.driverProfileId = acceptRideRequest.driverId
            ride.driverPosition = mapper.fromRequestPointToPoint(acceptRideRequest.location)
            return mapper.toRideResponse(repository.save(ride))
        } else {
            throw RideAlreadyAccepted(String
                .format("Ride with id: {id} is already accepted", acceptRideRequest.rideId))
        }
    }

    private fun getIfRidePresent(id: UUID) : Ride {
        val rideOptional = repository.findById(id)
        return rideOptional.orElseThrow { NoSuchRecordException(String
            .format("Ride with id: {%s} was not found", id))
        }
    }

    override fun confirmRideStart(rideId: UUID) {
        val ride = getIfRidePresent(rideId)
        if (!repository.canStartRide(rideId)) {
            throw RideStartConfirmationException(String
                .format("Ride cannot be started as the driver is too far from the pickup location"))
        }
        ride.status = RideStatus.IN_PROGRESS
        repository.save(ride)
    }

    override fun updateDriverPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse {
        val ride = getIfRidePresent(updatePositionRequest.rideId)
        ride.driverPosition = mapper.fromRequestPointToPoint(updatePositionRequest.location)
        repository.save(ride)
        return UpdatePositionResponse(
            updatePositionRequest.rideId,
            mapper.fromPointToResponsePoint(ride.passengerPosition),
            ride.status)
    }
}