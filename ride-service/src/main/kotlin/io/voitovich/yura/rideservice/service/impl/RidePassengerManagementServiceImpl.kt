package io.voitovich.yura.rideservice.service.impl

import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.request.CancelRequest
import io.voitovich.yura.rideservice.dto.request.CreateRideRequest
import io.voitovich.yura.rideservice.dto.request.SendRatingRequest
import io.voitovich.yura.rideservice.dto.request.UpdatePositionRequest
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.event.model.SendRatingModel
import io.voitovich.yura.rideservice.event.service.KafkaProducerService
import io.voitovich.yura.rideservice.exception.NoSuchRecordException
import io.voitovich.yura.rideservice.exception.RideAlreadyCanceled
import io.voitovich.yura.rideservice.exception.RideAlreadyPresented
import io.voitovich.yura.rideservice.exception.SendRatingException
import io.voitovich.yura.rideservice.repository.RideRepository
import io.voitovich.yura.rideservice.service.RidePassengerManagementService
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class RidePassengerManagementServiceImpl(val repository: RideRepository,
                                         val mapper: RideMapper,
                                         val producerService: KafkaProducerService) : RidePassengerManagementService {

    private val log = KotlinLogging.logger { }

    override fun createRide(request: CreateRideRequest): CreateRideResponse {
        log.info {"Creating ride for passenger with id: ${request.passengerId}" }
        if (repository.existsRideByPassengerProfileIdAndStatus(request.passengerId, RideStatus.REQUESTED)) {
            throw RideAlreadyPresented(String
                .format("Ride with requested status is already present for passenger with id: {%s}",
                    request.passengerId))
        }
        val ride = mapper.fromCreateRequestToEntity(request)

        val savedRide = repository.save(ride)
        return CreateRideResponse(request.passengerId, savedRide.id!!)
    }

    override fun rateDriver(request: SendRatingRequest) {
        val ride = getIfRidePresent(request.rideId)
        if (ride.status != RideStatus.IN_PROGRESS) {
            throw SendRatingException("You can't rate driver if ride is not in progress")
        }
        val model = SendRatingModel(
            ride.passengerProfileId,
            ride.driverProfileId!!,
            request.rating
        )
        producerService.rateDriver(model)
    }

    override fun updatePassengerPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse {
        log.info {"Updating position of passenger for ride with id: ${updatePositionRequest.rideId}"}
        val ride = getIfRidePresent(updatePositionRequest.rideId)
        ride.passengerPosition = mapper.fromRequestPointToPoint(updatePositionRequest.location)
        repository.save(ride)
        return UpdatePositionResponse(
            updatePositionRequest.rideId,
            mapper.fromPointToResponsePoint(ride.driverPosition),
            ride.status)
    }

    override fun cancelRide(cancelRequest: CancelRequest) {
        log.info {"Canceling ride with id: ${cancelRequest.rideId}" }
        val ride = getIfRidePresent(cancelRequest.rideId)
        if (ride.status != RideStatus.REQUESTED) {
            throw RideAlreadyCanceled(String
                .format("Ride with id: {%s} can't be canceled", cancelRequest.rideId))
        }
        ride.status = RideStatus.CANCELED
        repository.save(ride)
    }

    private fun getIfRidePresent(id: UUID) : Ride {
        val rideOptional = repository.findById(id)
        return rideOptional.orElseThrow { NoSuchRecordException(String
            .format("Ride with id: {%s} was not found", id))
        }
    }
}