package io.voitovich.yura.rideservice.service.impl

import io.voitovich.yura.rideservice.client.PassengerServiceClient
import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.event.model.ConfirmRatingReceiveModel
import io.voitovich.yura.rideservice.event.model.SendRatingModel
import io.voitovich.yura.rideservice.event.service.KafkaProducerService
import io.voitovich.yura.rideservice.exception.NoSuchRecordException
import io.voitovich.yura.rideservice.exception.RideAlreadyCanceledException
import io.voitovich.yura.rideservice.exception.RideCantBeStartedException
import io.voitovich.yura.rideservice.exception.SendRatingException
import io.voitovich.yura.rideservice.properties.DefaultApplicationProperties
import io.voitovich.yura.rideservice.repository.RideRepository
import io.voitovich.yura.rideservice.service.RidePassengerManagementService
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Service
class RidePassengerManagementServiceImpl(
    val repository: RideRepository,
    val mapper: RideMapper,
    val producerService: KafkaProducerService,
    var properties: DefaultApplicationProperties,
    var passengerServiceClient: PassengerServiceClient) : RidePassengerManagementService {

    private val log = KotlinLogging.logger { }

    private companion object {
        private val ALLOWED_RIDE_START_STATUSES = setOf(RideStatus.ACCEPTED, RideStatus.COMPLETED)
        private const val RATE_DRIVER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE = "You can't rate driver if ride is not in progress"
        private const val NO_SUCH_RECORD_EXCEPTION_MESSAGE = "Ride with id: {%s} was not found"
        private const val RIDE_ALREADY_CANCELED_EXCEPTION_MESSAGE = "Ride with id: {%s} can't be canceled"
        private const val RIDE_CANT_BE_STARTED_EXCEPTION_MESSAGE = "Ride for passenger with id: {%s} can not be started, complete or cancel current ride and repeat request"
        private const val RATE_DRIVER_TIME_NOT_ALLOWED_EXCEPTION_MESSAGE = "Rating cannot be submitted after the specified time: {%s}h has elapsed after the completion of the ride"
    }

    fun checkPassengerExistence(id: UUID) {

        passengerServiceClient.getPassengerProfile(id)
    }
    override fun createRide(request: CreateRideRequest): CreateRideResponse {
        log.info {"Creating ride for passenger with id: ${request.passengerId}" }

        checkPassengerExistence(request.passengerId)

        if (repository.existsRideByPassengerProfileIdAndStatusIsNotIn(request.passengerId,
                ALLOWED_RIDE_START_STATUSES)) {
            throw RideCantBeStartedException(String
                .format(RIDE_CANT_BE_STARTED_EXCEPTION_MESSAGE,
                    request.passengerId))
        }
        val ride = mapper.fromCreateRequestToEntity(request)

        val savedRide = repository.save(ride)
        return CreateRideResponse(request.passengerId, savedRide.id!!)
    }

    private fun checkRideCanBeRated(ride: Ride) {
        if (ride.status !in setOf(RideStatus.IN_PROGRESS, RideStatus.COMPLETED)) {
            throw SendRatingException(RATE_DRIVER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE)
        }

        if (ride.status == RideStatus.COMPLETED) {
            val duration = Duration.between(ride.endDate, LocalDateTime.now()).toHours()
            println(duration)
            if (duration > properties.allowedRatingTimeInHours) {
                throw SendRatingException(String.format(RATE_DRIVER_TIME_NOT_ALLOWED_EXCEPTION_MESSAGE,
                    properties.allowedRatingTimeInHours))
            }
        }

    }
    override fun rateDriver(request: SendRatingRequest) {
        val ride = getIfRidePresent(request.rideId)
        checkRideCanBeRated(ride);
        val model = SendRatingModel(
            raterId = ride.passengerProfileId,
            ratedId = ride.driverProfileId!!,
            rating = request.rating,
            rideId = request.rideId,
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

    override fun confirmPassengerRated(model: ConfirmRatingReceiveModel) {
        log.info { "Confirming passenger rated with model: $model" }
        val ride = getIfRidePresent(model.rideId)
        ride.passengerRating = model.rating
        repository.save(ride)
    }

    override fun cancelRide(cancelRequest: CancelRequest) {
        log.info {"Canceling ride with id: ${cancelRequest.rideId}" }
        val ride = getIfRidePresent(cancelRequest.rideId)
        if (ride.status != RideStatus.REQUESTED) {
            throw RideAlreadyCanceledException(String
                .format(RIDE_ALREADY_CANCELED_EXCEPTION_MESSAGE, cancelRequest.rideId))
        }
        ride.status = RideStatus.CANCELED
        repository.save(ride)
    }

    private fun getIfRidePresent(id: UUID) : Ride {
        val rideOptional = repository.findById(id)
        return rideOptional.orElseThrow { NoSuchRecordException(String
            .format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, id))
        }
    }

    override fun getAllRides(passengerId: UUID, request: RidePageRequest) : RidePageResponse {
        log.info { "Retrieving rides for passenger with id ${passengerId} for page ${request.pageNumber} " +
                "with size ${request.pageSize} " +
                "and ordering by ${request.orderBy}" }
        val page = repository.getRidesByPassengerProfileId(passengerId, PageRequest
            .of(request.pageNumber - 1,
                request.pageSize,
                Sort.by(request.orderBy)))
        return RidePageResponse(page
            .content.stream()
            .map {t-> mapper.toRideResponse(t)}.toList(),
            request.pageNumber,
            page.totalElements,
            page.totalPages)
    }
}