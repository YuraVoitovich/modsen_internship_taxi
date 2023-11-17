package io.voitovich.yura.rideservice.service.impl

import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.*
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.exception.NoSuchRecordException
import io.voitovich.yura.rideservice.exception.RideAlreadyAccepted
import io.voitovich.yura.rideservice.repository.RideRepository
import io.voitovich.yura.rideservice.service.RideService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*

@Service
class RideServiceImpl(val repository: RideRepository, val mapper: RideMapper) : RideService {
    override fun getRideById(id: UUID): RideResponse {
        val ride = repository.findById(id)

        return mapper.toRideResponse(ride
            .orElseThrow { NoSuchRecordException(String
            .format("Ride with id: {%s} was not found", id))}
        )
    }

    override fun deleteRideById(id: UUID) {
        val ride = repository.findById(id);
        if (ride.isEmpty) {
            throw NoSuchRecordException(String.format("Ride with id: {%s} was not found", id))
        }
        repository.deleteById(id)
    }

    override fun getRidePage(pageRideRequest: RidePageRequest): RidePageResponse {
        val page = repository.findAll(PageRequest
            .of(pageRideRequest.pageNumber - 1,
                pageRideRequest.pageSize,
                Sort.by(pageRideRequest.orderBy)))
        return RidePageResponse(page
            .content.stream()
            .map {t-> mapper.toRideResponse(t)}.toList(),
            pageRideRequest.pageNumber,
            page.totalElements,
            page.totalPages)
    }

    override fun createRide(request: CreateRideRequest): CreateRideResponse {
        val ride = mapper.fromCreateRequestToEntity(request)
        val savedRide = repository.save(ride)
        return CreateRideResponse(request.passengerId, savedRide.id!!)
    }

    override fun getAvailableRides(getAvailableRidesRequest: GetAvailableRidesRequest): GetAvailableRidesResponse {
        val rides = repository.getDriverAvailableRides(mapper
            .fromRequestPointToPoint(getAvailableRidesRequest.currentLocation),
            1000000)
        return GetAvailableRidesResponse(rides.map { t -> mapper.toAvailableRideResponse(t) }.toList())
    }

    override fun acceptRide(acceptRideRequest: AcceptRideRequest) {
        val rideOptional = repository.findById(acceptRideRequest.rideId)
        val ride = rideOptional.orElseThrow { NoSuchRecordException(String
            .format("Ride with id: {%s} was not found", acceptRideRequest.rideId))}
        if (ride.status == RideStatus.REQUESTED) {
            ride.status = RideStatus.ACCEPTED
            ride.driverProfileId = acceptRideRequest.driverId
            repository.save(ride)
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
    override fun updateDriverPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse {
        val ride = getIfRidePresent(updatePositionRequest.id);
        ride.driverPosition = mapper.fromRequestPointToPoint(updatePositionRequest.location)
        repository.save(ride)
        return UpdatePositionResponse(mapper.fromPointToResponsePoint(ride.passengerPosition))
    }

    override fun updatePassengerPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse {
        val ride = getIfRidePresent(updatePositionRequest.id);
        ride.passengerPosition = mapper.fromRequestPointToPoint(updatePositionRequest.location)
        repository.save(ride)
        return UpdatePositionResponse(mapper.fromPointToResponsePoint(ride.driverPosition))
    }
}