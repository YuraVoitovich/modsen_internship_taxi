package io.voitovich.yura.rideservice.service.impl

import io.voitovich.yura.rideservice.client.service.DriverClientService
import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.event.model.ConfirmRatingReceiveModel
import io.voitovich.yura.rideservice.event.model.SendRatingModel
import io.voitovich.yura.rideservice.event.service.KafkaProducerService
import io.voitovich.yura.rideservice.exception.*
import io.voitovich.yura.rideservice.properties.DefaultApplicationProperties
import io.voitovich.yura.rideservice.repository.RideRepository
import io.voitovich.yura.rideservice.service.RideDriverManagementService
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Service
class RideDriverManagementServiceImpl(
    val repository: RideRepository,
    val mapper: RideMapper,
    val producerService : KafkaProducerService,
    var driverService: DriverClientService,
    val properties: DefaultApplicationProperties,
    val clock: Clock, ) : RideDriverManagementService {


    private val log = KotlinLogging.logger { }

    companion object {
        const val RATE_PASSENGER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE = "You can't rate passenger if ride is not in progress or if ride is not completed"
        const val RIDE_END_INVALID_DRIVER_LOCATION_EXCEPTION_MESSAGE = "Ride cannot be ended as the driver is too far from the pickup location"
        const val RIDE_END_INVALID_STATUS_EXCEPTION_MESSAGE = "Ride cannot be ended as the ride status is not IN_PROGRESS"
        const val RIDE_START_INVALID_DRIVER_LOCATION_EXCEPTION_MESSAGE = "Ride cannot be started as the driver is too far from the pickup location"
        const val RIDE_START_INVALID_STATUS_EXCEPTION_MESSAGE = "Ride cannot be started as the ride status is not ACCEPTED"
        const val NO_SUCH_RECORD_EXCEPTION_MESSAGE = "Ride with id: {%s} was not found"
        const val RIDE_ALREADY_ACCEPTED_EXCEPTION_MESSAGE = "Ride with id: {id} is already accepted"
        const val NOT_VALID_SEARCH_RADIUS_EXCEPTION_MESSAGE = "Search radius must be in range {%d}:{%d}"
        const val RATE_PASSENGER_TIME_NOT_ALLOWED_EXCEPTION_MESSAGE = "Rating cannot be submitted after the specified time: {%s}h has elapsed after the completion of the ride"
    }

    private fun getRadius(userRadius: Int?) : Int {
        if (userRadius == null) {
            return properties.searchRadius
        }
        if (userRadius <= properties.maxRadius && userRadius >= properties.minRadius) {
            return userRadius
        }
        if (properties.useDefaultRadiusIfRadiusNotInRange.not()) {
            throw NotValidSearchRadiusException(
                String
                    .format(NOT_VALID_SEARCH_RADIUS_EXCEPTION_MESSAGE, properties.minRadius, properties.maxRadius)
            )
        }
        return properties.searchRadius
    }
    override fun getAvailableRides(getAvailableRidesRequest: GetAvailableRidesRequest): GetAvailableRidesResponse {
        val radius = getRadius(getAvailableRidesRequest.radius)
        log.info("Getting all available rides with radius: $radius for driver with id: ${getAvailableRidesRequest.id}")
        val rides = repository.getDriverAvailableRides(mapper
            .fromRequestPointToPoint(getAvailableRidesRequest.currentLocation),
            radius)
        return GetAvailableRidesResponse(rides
            .map { t -> mapper.toAvailableRideResponse(t) }.toList())
    }

    override fun acceptRide(acceptRideRequest: AcceptRideRequest) {
        log.info { "Accepting ride with id: ${acceptRideRequest.rideId}" }

        driverService.getDriverProfile(acceptRideRequest.driverId)

        val ride = getIfRidePresent(acceptRideRequest.rideId)
        if (ride.status != RideStatus.REQUESTED) {
            throw RideAlreadyAcceptedException(String
                .format(RIDE_ALREADY_ACCEPTED_EXCEPTION_MESSAGE, acceptRideRequest.rideId))
        }
        ride.status = RideStatus.ACCEPTED
        ride.driverProfileId = acceptRideRequest.driverId
        ride.driverPosition = mapper.fromRequestPointToPoint(acceptRideRequest.location)
        repository.save(ride)
    }


    private fun checkRideCanBeRated(ride: Ride) {
        if (ride.status !in setOf(RideStatus.IN_PROGRESS, RideStatus.COMPLETED)) {
            throw SendRatingException(RATE_PASSENGER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE)
        }

        if (ride.status == RideStatus.COMPLETED) {
            val duration = Duration.between(ride.endDate, LocalDateTime.now(clock)).toHours()
            if (duration > properties.allowedRatingTimeInHours) {
                throw SendRatingException(String.format(RATE_PASSENGER_TIME_NOT_ALLOWED_EXCEPTION_MESSAGE, properties.allowedRatingTimeInHours))
            }
        }

    }

    override fun ratePassenger(request: SendRatingRequest) {
        val ride = getIfRidePresent(request.rideId)
        checkRideCanBeRated(ride)
        val model = SendRatingModel(
            ratedId = ride.passengerProfileId,
            raterId = ride.driverProfileId!!,
            rating = request.rating,
            rideId = request.rideId,
        )
        producerService.ratePassenger(model)
    }

    override fun getAllRides(driverId: UUID, request: RidePageRequest) : RidePageResponse {
        log.info { "Retrieving rides for driver with id $driverId for page ${request.pageNumber} " +
                "with size ${request.pageSize} " +
                "and ordering by ${request.orderBy}" }

        val page = repository.getRidesByDriverProfileId(driverId, PageRequest
            .of(request.pageNumber - 1,
                request.pageSize,
                Sort.by(request.orderBy)))
        return RidePageResponse(
            profiles = mapper.toDriverRideResponses(page.content),
            pageNumber = request.pageNumber,
            totalElements = page.totalElements,
            totalPages = page.totalPages
        )
    }

    private fun getIfRidePresent(id: UUID) : Ride {
        val rideOptional = repository.findById(id)
        return rideOptional.orElseThrow { NoSuchRecordException(String
            .format("Ride with id: {%s} was not found", id))
        }
    }

    override fun confirmRideStart(rideId: UUID) {
        log.info { "Confirming the start of the ride with id: $rideId" }
        val ride = getIfRidePresent(rideId)
        if (ride.status != RideStatus.ACCEPTED) {
            throw RideStartConfirmationException(String
                .format(RIDE_START_INVALID_STATUS_EXCEPTION_MESSAGE))
        }
        if (!repository.canStartRide(rideId)) {
            throw RideStartConfirmationException(String
                .format(RIDE_START_INVALID_DRIVER_LOCATION_EXCEPTION_MESSAGE))
        }
        ride.startDate = LocalDateTime.now(clock)
        ride.status = RideStatus.IN_PROGRESS
        repository.save(ride)
    }

    override fun confirmRideEnd(rideId: UUID) {
        log.info { "Confirming the end of the ride with id: $rideId" }
        val ride = getIfRidePresent(rideId)
        if (ride.status != RideStatus.IN_PROGRESS) {
            throw RideEndConfirmationException(String
                .format(RIDE_END_INVALID_STATUS_EXCEPTION_MESSAGE))
        }
        if (!repository.canEndRide(rideId)) {
            throw RideEndConfirmationException(String
                .format(RIDE_END_INVALID_DRIVER_LOCATION_EXCEPTION_MESSAGE))
        }
        ride.endDate = LocalDateTime.now(clock)
        ride.status = RideStatus.COMPLETED
        repository.save(ride)
    }

    override fun confirmDriverRated(model: ConfirmRatingReceiveModel) {
        log.info { "Confirming driver rated with model: $model" }
        val ride = getIfRidePresent(model.rideId)
        ride.driverRating = model.rating
        repository.save(ride)
    }

    override fun updateDriverPosition(updatePositionRequest: UpdatePositionRequest): UpdatePositionResponse {
        log.info { "Updating the position of the driver with id: ${updatePositionRequest.id}"}
        val ride = getIfRidePresent(updatePositionRequest.rideId)
        ride.driverPosition = mapper.fromRequestPointToPoint(updatePositionRequest.location)
        repository.save(ride)
        return UpdatePositionResponse(
            updatePositionRequest.rideId,
            mapper.fromPointToResponsePoint(ride.passengerPosition),
            ride.status)
    }
}