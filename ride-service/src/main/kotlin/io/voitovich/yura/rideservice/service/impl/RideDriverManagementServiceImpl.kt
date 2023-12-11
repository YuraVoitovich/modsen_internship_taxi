package io.voitovich.yura.rideservice.service.impl

import io.voitovich.yura.rideservice.client.DriverServiceClient
import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.request.*
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
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
import java.time.LocalDateTime
import java.util.*
import java.time.Duration

@Service
class RideDriverManagementServiceImpl(
    val repository: RideRepository,
    val mapper: RideMapper,
    val producerService : KafkaProducerService,
    var driverServiceClient: DriverServiceClient,
    val properties: DefaultApplicationProperties) : RideDriverManagementService {


    private val log = KotlinLogging.logger { }

    private companion object {
        private const val RATE_PASSENGER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE = "You can't rate passenger if ride is not in progress or if ride is not completed"
        private const val RIDE_END_CONFIRMATION_EXCEPTION_MESSAGE = "Ride cannot be ended as the driver is too far from the pickup location"
        private const val RIDE_START_CONFIRMATION_EXCEPTION_MESSAGE = "Ride cannot be started as the driver is too far from the pickup location"
        private const val NO_SUCH_RECORD_EXCEPTION_MESSAGE = "Ride with id: {%s} was not found"
        private const val RIDE_ALREADY_ACCEPTED_EXCEPTION_MESSAGE = "Ride with id: {id} is already accepted"
        private const val NOT_VALID_SEARCH_RADIUS_EXCEPTION_MESSAGE = "Search radius must be in range {%d}:{%d}"
        private const val RATE_PASSENGER_TIME_NOT_ALLOWED_EXCEPTION_MESSAGE = "Rating cannot be submitted after the specified time: {%s}h has elapsed after the completion of the ride"
    }

    private fun getRadius(userRadius: Int?) : Int {
        if (userRadius == null) {
            return properties.searchRadius
        }
        if (userRadius < properties.maxRadius && userRadius > properties.minRadius) {
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
        val radius = getRadius(getAvailableRidesRequest.radius);
        log.info("Getting all available rides with radius: $radius for driver with id: ${getAvailableRidesRequest.id}")
        val rides = repository.getDriverAvailableRides(mapper
            .fromRequestPointToPoint(getAvailableRidesRequest.currentLocation),
            radius)
        return GetAvailableRidesResponse(rides
            .map { t -> mapper.toAvailableRideResponse(t) }.toList())
    }

    override fun acceptRide(acceptRideRequest: AcceptRideRequest) {
        log.info { "Accepting ride with id: ${acceptRideRequest.rideId}" }

        driverServiceClient.getDriverProfile(acceptRideRequest.driverId)

        val rideOptional = repository.findById(acceptRideRequest.rideId)
        val ride = rideOptional.orElseThrow { NoSuchRecordException(String
            .format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, acceptRideRequest.rideId))
        }
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
            val duration = Duration.between(ride.endDate, LocalDateTime.now()).toHours()
            println(duration)
            if (duration > properties.allowedRatingTimeInHours) {
                throw SendRatingException(String.format(RATE_PASSENGER_TIME_NOT_ALLOWED_EXCEPTION_MESSAGE, properties.allowedRatingTimeInHours))
            }
        }

    }

    override fun ratePassenger(request: SendRatingRequest) {
        val ride = getIfRidePresent(request.rideId)
        checkRideCanBeRated(ride);
        val model = SendRatingModel(
            ratedId = ride.passengerProfileId,
            raterId = ride.driverProfileId!!,
            rating = request.rating,
            rideId = request.rideId,
        )
        producerService.ratePassenger(model)
    }

    override fun getAllRides(driverId: UUID, request: RidePageRequest) : RidePageResponse {
        log.info { "Retrieving rides for driver with id ${driverId} for page ${request.pageNumber} " +
                "with size ${request.pageSize} " +
                "and ordering by ${request.orderBy}" }
        val page = repository.getRidesByDriverProfileId(driverId, PageRequest
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

    private fun getIfRidePresent(id: UUID) : Ride {
        val rideOptional = repository.findById(id)
        return rideOptional.orElseThrow { NoSuchRecordException(String
            .format("Ride with id: {%s} was not found", id))
        }
    }

    override fun confirmRideStart(rideId: UUID) {
        log.info { "Confirming the start of the ride with id: $rideId" }
        val ride = getIfRidePresent(rideId)
        if (!repository.canStartRide(rideId)) {
            throw RideStartConfirmationException(String
                .format(RIDE_START_CONFIRMATION_EXCEPTION_MESSAGE))
        }
        ride.startDate = LocalDateTime.now()
        ride.status = RideStatus.IN_PROGRESS
        repository.save(ride)
    }

    override fun confirmRideEnd(rideId: UUID) {
        log.info { "Confirming the end of the ride with id: $rideId" }
        val ride = getIfRidePresent(rideId)
        if (!repository.canEndRide(rideId)) {
            throw RideEndConfirmationException(String
                .format(RIDE_END_CONFIRMATION_EXCEPTION_MESSAGE))
        }
        ride.endDate = LocalDateTime.now()
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