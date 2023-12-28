package io.voitovich.yura.rideservice.unit

import io.voitovich.yura.rideservice.client.service.DriverClientService
import io.voitovich.yura.rideservice.client.service.PassengerClientService
import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.mapper.impl.RideMapperImpl
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.event.model.SendRatingModel
import io.voitovich.yura.rideservice.event.service.KafkaProducerService
import io.voitovich.yura.rideservice.exception.RideAlreadyCanceledException
import io.voitovich.yura.rideservice.exception.RideCantBeStartedException
import io.voitovich.yura.rideservice.exception.SendRatingException
import io.voitovich.yura.rideservice.properties.DefaultApplicationProperties
import io.voitovich.yura.rideservice.repository.RideRepository
import io.voitovich.yura.rideservice.service.impl.RidePassengerManagementServiceImpl
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultCancelRequest
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultConfirmRatingReceiveModel
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultCreateRideRequest
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultPoint
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@ExtendWith(MockitoExtension::class)
class RidePassengerManagementServiceImplTest {

    @Mock
    private lateinit var repository: RideRepository

    private lateinit var mapper: RideMapper

    private lateinit var properties: DefaultApplicationProperties

    @Mock
    private lateinit var passengerClientService: PassengerClientService

    @Mock
    private lateinit var driverClientService: DriverClientService

    @Mock
    private lateinit var producerService: KafkaProducerService

    private lateinit var service: RidePassengerManagementServiceImpl

    @BeforeEach
    fun setUp() {
        //clock = Clock.fixed(Instant.parse("2023-01-01T00:00:00Z"), ZoneId.systemDefault())
        mapper = RideMapperImpl(driverClientService, passengerClientService)
        properties = DefaultApplicationProperties()

        service = RidePassengerManagementServiceImpl(
            repository = repository,
            mapper = mapper,
            producerService = producerService,
            passengerService = passengerClientService,
            properties = properties
        )

    }

    @Test
    fun createRide_correctRequest_shouldCreateRide() {
        // Arrange
        val request = createDefaultCreateRideRequest()
        val passengerId = request.passengerId
        val rideId = UUID.randomUUID()

        val rideToSave = mapper.fromCreateRequestToEntity(request)
        val rideToReturn = mapper.fromCreateRequestToEntity(request)
        rideToReturn.id = rideId

        val expectedResponse = CreateRideResponse(passengerId, rideId)

        doReturn(false)
            .`when`(repository).existsRideByPassengerProfileIdAndStatusIsNotIn(
                passengerId,
                setOf(RideStatus.ACCEPTED, RideStatus.COMPLETED))

        doReturn(rideToReturn).`when`(repository).save(rideToSave)

        // Act
        val response = service.createRide(request)

        // Assert
        verify(repository, times(1))
            .existsRideByPassengerProfileIdAndStatusIsNotIn(passengerId, setOf(RideStatus.ACCEPTED, RideStatus.COMPLETED))
        verify(repository, times(1)).save(rideToSave)

        assertEquals(expectedResponse, response)
    }

    @Test
    fun createRide_rideCanNotBeStarted_shouldThrowRideCantBeStartedException() {
        // Arrange
        val request = createDefaultCreateRideRequest()
        val passengerId = request.passengerId

        val rideToSave = mapper.fromCreateRequestToEntity(request)

        doReturn(true)
            .`when`(repository).existsRideByPassengerProfileIdAndStatusIsNotIn(
                passengerId,
                setOf(RideStatus.ACCEPTED, RideStatus.COMPLETED))

        // Act and Assert
        assertThrows<RideCantBeStartedException> { service.createRide(request) }

        verify(repository, times(1))
            .existsRideByPassengerProfileIdAndStatusIsNotIn(passengerId, setOf(RideStatus.ACCEPTED, RideStatus.COMPLETED))
    }

    @Test
    fun rateDriver_correctRequest_DriverRated() {
        // Arrange
        val request = UnitTestsUtils.createDefaultSendRatingRequest()
        val rideId = request.rideId

        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.IN_PROGRESS
        )
            .id(rideId)
            .driverProfileId(UUID.randomUUID())
            .build()

        val expectedSendRatingModel = SendRatingModel(
            rideId = rideId,
            raterId = ride.passengerProfileId,
            ratedId = ride.driverProfileId!!,
            rating = request.rating
        )

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        // Act
        service.rateDriver(request)

        // Assert
        verify(repository, times(1)).findById(rideId)
        verify(producerService, times(1)).rateDriver(expectedSendRatingModel)
    }

    @Test
    fun rateDriver_incorrectRideStatus_shouldThrowSendRatingException() {
        // Arrange
        val request = UnitTestsUtils.createDefaultSendRatingRequest()
        val rideId = request.rideId

        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.REQUESTED
        )
            .id(rideId)
            .driverProfileId(UUID.randomUUID())
            .build()

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        // Act and Assert
        assertThrows<SendRatingException> { service.rateDriver(request) }

        verify(repository, times(1)).findById(rideId)
    }

    @Test
    fun rateDriver_notAllowedTimeAfterRideCompleted_shouldThrowSendRatingException() {
        // Arrange
        val request = UnitTestsUtils.createDefaultSendRatingRequest()
        val rideId = request.rideId

        val rideEndClock = Clock.fixed(Instant.parse("2022-01-01T00:00:00Z"), ZoneId.systemDefault())

        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.COMPLETED
        )
            .id(rideId)
            .driverProfileId(UUID.randomUUID())
            .endDate(LocalDateTime.now(rideEndClock))
            .build()

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        // Act and Assert
        assertThrows<SendRatingException> { service.rateDriver(request) }

        verify(repository, times(1)).findById(rideId)
    }

    @Test
    fun confirmPassengerRated_correctRequest_confirmPassengerRated() {
        // Arrange
        val model = createDefaultConfirmRatingReceiveModel()
        val rideId = model.rideId

        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.COMPLETED
        )
            .id(rideId)
            .driverProfileId(UUID.randomUUID())
            .build()

        val expectedRide = Ride.builder(
            passengerProfileId = ride.passengerProfileId,
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.COMPLETED
        )
            .id(rideId)
            .driverProfileId(ride.driverProfileId)
            .passengerRating(model.rating)
            .build()

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        // Act
        service.confirmPassengerRated(model = model)

        // Assert
        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).save(expectedRide)
    }

    @Test
    fun updatePassengerPosition_correctUpdateRequest_updatePassengerPosition() {
        // Arrange
        val request = UnitTestsUtils.createDefaultUpdatePositionRequest()
        val rideId = request.rideId

        val ride = Ride.builder(passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.COMPLETED)
            .id(rideId)
            .driverProfileId(UUID.randomUUID())
            .driverPosition(mapper.fromRequestPointToPoint(request.location))
            .build()

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        val expectedSaveRide = Ride.builder(passengerProfileId = ride.passengerProfileId,
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.COMPLETED)
            .id(rideId)
            .driverProfileId(ride.driverProfileId)
            .driverPosition(mapper.fromRequestPointToPoint(request.location))
            .passengerPosition(mapper.fromRequestPointToPoint(request.location))
            .build()

        val expectedResult = UpdatePositionResponse(
            rideId = rideId,
            userPosition = mapper.fromPointToResponsePoint(ride.driverPosition),
            status = RideStatus.COMPLETED,
        )

        // Act
        val result = service.updatePassengerPosition(request)

        // Assert
        assertEquals(expectedResult, result)
        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).save(expectedSaveRide)
    }

    @Test
    fun getAllRides_correctRequest_returnRidePage() {
        // Arrange
        val passengerId = UUID.randomUUID()
        val request = UnitTestsUtils.createDefaultRidePageRequest()
        val pageRequest = PageRequest.of(request.pageNumber - 1, request.pageSize, Sort.by(request.orderBy))
        val page = PageImpl<Ride>(listOf())
        doReturn(page).`when`(repository).getRidesByPassengerProfileId(passengerId, pageRequest)
        val expectedResult = RidePageResponse(
            listOf(),
            1,
            0,
            1)

        // Act
        val result = service.getAllRides(passengerId, request)

        // Assert
        assertEquals(expectedResult, result)
        verify(repository, times(1)).getRidesByPassengerProfileId(passengerId, pageRequest)
    }

    @Test
    fun cancelRide_correctRequest_shouldCancelRide() {
        // Arrange
        val cancelRequest = createDefaultCancelRequest()
        val rideId = cancelRequest.rideId

        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.REQUESTED
        )
            .id(rideId)
            .build()

        doReturn(Optional.of(ride)).`when`(repository).findById(rideId)

        // Act
        service.cancelRide(cancelRequest)

        // Assert
        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).save(ride)

        assertEquals(RideStatus.CANCELED, ride.status)
    }

    @Test
    fun cancelRide_rideAlreadyCanceled_shouldThrowRideAlreadyCanceledException() {
        // Arrange
        val cancelRequest = createDefaultCancelRequest()
        val rideId = cancelRequest.rideId

        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.COMPLETED
        )
            .id(rideId)
            .build()

        doReturn(Optional.of(ride)).`when`(repository).findById(rideId)

        // Act and Assert
        assertThrows<RideAlreadyCanceledException> { service.cancelRide(cancelRequest) }

        verify(repository, times(1)).findById(rideId)
    }


}