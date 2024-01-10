package io.voitovich.yura.rideservice.unit

import io.voitovich.yura.rideservice.client.service.DriverClientService
import io.voitovich.yura.rideservice.client.service.PassengerClientService
import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.mapper.impl.RideMapperImpl
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.event.model.SendRatingModel
import io.voitovich.yura.rideservice.event.service.KafkaProducerService
import io.voitovich.yura.rideservice.exception.*
import io.voitovich.yura.rideservice.model.RideProjection
import io.voitovich.yura.rideservice.properties.DefaultApplicationProperties
import io.voitovich.yura.rideservice.repository.RideRepository
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultAcceptRideRequest
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultConfirmRatingReceiveModel
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultGetAvailableRidesRequest
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultGetAvailableRidesRequestWithInvalidRadius
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultGetAvailableRidesRequestWithoutRadius
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultPoint
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultRidePageRequest
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultSendRatingRequest
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultUpdatePositionRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.TestPropertySource
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


@TestPropertySource(locations = ["classpath:application-test.yml"])
@ExtendWith(MockitoExtension::class)
class RideDriverManagementServiceImplTest {


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

    private lateinit var service: RideDriverManagementServiceImpl

    private lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        clock = Clock.fixed(Instant.parse("2023-01-01T00:00:00Z"), ZoneId.systemDefault())
        mapper = RideMapperImpl(driverClientService, passengerClientService)
        properties = DefaultApplicationProperties()
        // Устанавливаем свойства вручную
        properties.minRadius = 300
        properties.maxRadius = 1000
        properties.searchRadius = 500
        properties.useDefaultRadiusIfRadiusNotInRange = false
        properties.allowedRatingTimeInHours = 1
        service = RideDriverManagementServiceImpl(
            repository = repository,
            mapper = mapper,
            producerService = producerService,
            driverService = driverClientService,
            properties = properties,
            clock = clock
        )

    }
    @Test
    fun getAvailableRides_correctRequest_shouldReturnRide() {
        // Arrange
        val getAvailableRidesRequest = createDefaultGetAvailableRidesRequest()
        val radius: Int = getAvailableRidesRequest.radius!!
        val point = mapper.fromRequestPointToPoint(getAvailableRidesRequest.currentLocation)
        doReturn(listOf<RideProjection>()).`when`(repository)
            .getDriverAvailableRides(point, radius)

        // Act
        service.getAvailableRides(getAvailableRidesRequest)

        // Assert
        verify(repository, times(1)).getDriverAvailableRides(point, radius = radius)
    }


    @Test
    fun getAvailableRides_correctRequestWithoutRadius_shouldReturnRide() {
        // Arrange
        val getAvailableRidesRequest = createDefaultGetAvailableRidesRequestWithoutRadius()
        val point = mapper.fromRequestPointToPoint(getAvailableRidesRequest.currentLocation)
        doReturn(listOf<RideProjection>()).`when`(repository)
            .getDriverAvailableRides(point, properties.searchRadius)

        // Act
        service.getAvailableRides(getAvailableRidesRequest)

        // Assert
        verify(repository, times(1)).getDriverAvailableRides(point, properties.searchRadius)
    }

    @Test
    fun getAvailableRides_incorrectRadiusUseDefaultRadiusFalse_shouldThrowNotValidSearchRadiusException() {
        // Arrange
        val getAvailableRidesRequest = createDefaultGetAvailableRidesRequestWithInvalidRadius()

        // Act and Assert
        assertThrows<NotValidSearchRadiusException> { service.getAvailableRides(getAvailableRidesRequest) }
    }

    @Test
    fun getAvailableRides_incorrectRadiusUseDefaultRadiusTrue_shouldThrowNotValidSearchRadiusException() {
        // Arrange
        val getAvailableRidesRequest = createDefaultGetAvailableRidesRequestWithInvalidRadius()
        properties.useDefaultRadiusIfRadiusNotInRange = true
        val radius = 500
        val point = mapper.fromRequestPointToPoint(getAvailableRidesRequest.currentLocation)
        doReturn(listOf<RideProjection>()).`when`(repository)
            .getDriverAvailableRides(point, radius)

        // Act
        service.getAvailableRides(getAvailableRidesRequest)

        // Assert
        verify(repository, times(1)).getDriverAvailableRides(point, radius)
    }


    @Test
    fun acceptRide_correctRequest_shouldAcceptRide() {
        // Arrange
        val acceptRideRequest = createDefaultAcceptRideRequest()
        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.REQUESTED
        )
            .id(acceptRideRequest.rideId)
            .build()

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(acceptRideRequest.rideId)

        val expectedRide = Ride.builder(
            passengerProfileId = ride.passengerProfileId,
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.ACCEPTED
        )
            .id(acceptRideRequest.rideId)
            .driverProfileId(acceptRideRequest.driverId)
            .driverPosition(mapper.fromRequestPointToPoint(acceptRideRequest.location))
            .build()

        // Act
        service.acceptRide(acceptRideRequest)

        // Assert
        verify(repository, times(1)).findById(acceptRideRequest.rideId)
        verify(repository, times(1)).save(expectedRide)
    }

    @Test
    fun acceptRide_wrongRideStatus_shouldThrowRideAlreadyAcceptedException() {
        // Arrange
        val acceptRideRequest = createDefaultAcceptRideRequest()
        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.ACCEPTED
        )
            .id(acceptRideRequest.rideId)
            .driverProfileId(null)
            .build()

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(acceptRideRequest.rideId)

        // Act and Assert
        assertThrows<RideAlreadyAcceptedException> { service.acceptRide(acceptRideRequest) }

        // Verify
        verify(repository, times(1)).findById(acceptRideRequest.rideId)
    }

    @Test
    fun acceptRide_rideNotFound_shouldThrowNoSuchRecordException() {
        // Arrange
        val acceptRideRequest = createDefaultAcceptRideRequest()

        doReturn(Optional.empty<Ride>()).`when`(repository)
            .findById(acceptRideRequest.rideId)

        // Act and Assert
        assertThrows<NoSuchRecordException> { service.acceptRide(acceptRideRequest) }

        // Verify
        verify(repository, times(1)).findById(acceptRideRequest.rideId)
    }


    @Test
    fun confirmRideEnd_correctRequest_shouldConfirmRideEnd() {
        // Arrange
        val rideId = UUID.randomUUID()
        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.REQUESTED
        )
            .id(rideId)
            .driverProfileId(null)
            .build()

        val expectedRide = Ride.builder(
            passengerProfileId = ride.passengerProfileId,
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.COMPLETED
        )
            .id(rideId)
            .driverProfileId(null)
            .endDate(LocalDateTime.now(clock))
            .build()

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        doReturn(true).`when`(repository).canEndRide(rideId)

        // Act
        service.confirmRideEnd(rideId)

        // Assert
        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).canEndRide(rideId)
        verify(repository, times(1)).save(expectedRide)
    }

    @Test
    fun confirmRideEnd_rideNotFound_shouldThrowNoSuchRecordException() {
        // Arrange
        val rideId = UUID.randomUUID()

        doReturn(Optional.empty<Ride>()).`when`(repository)
            .findById(rideId)

        // Act and Assert
        assertThrows<NoSuchRecordException> { service.confirmRideEnd(rideId) }

        // Verify
        verify(repository, times(1)).findById(rideId)
    }

    @Test
    fun confirmRideStart_rideCantBeStarted_shouldThrowRideStartConfirmationException() {
        // Arrange
        val rideId = UUID.randomUUID()
        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.REQUESTED
        )
            .id(rideId)
            .driverProfileId(null)
            .build()

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        doReturn(false).`when`(repository).canStartRide(rideId)

        // Act and Assert
        assertThrows<RideStartConfirmationException> { service.confirmRideStart(rideId) }

        // Verify
        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).canStartRide(rideId)
    }




    @Test
    fun confirmRideStart_correctRequest_shouldConfirmRideStart() {
        // Arrange
        val rideId = UUID.randomUUID()
        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.REQUESTED
        )
            .id(rideId)
            .driverProfileId(null)
            .build()

        val expectedRide = Ride.builder(
            passengerProfileId = ride.passengerProfileId,
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.IN_PROGRESS
        )
            .id(rideId)
            .driverProfileId(null)
            .startDate(LocalDateTime.now(clock))
            .build()

        doReturn(Optional.of(ride)).`when`(repository).findById(rideId)
        doReturn(true).`when`(repository).canStartRide(rideId)

        // Act
        service.confirmRideStart(rideId)

        // Assert
        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).canStartRide(rideId)
        verify(repository, times(1)).save(expectedRide)
    }

    @Test
    fun confirmRideStart_rideNotFound_shouldThrowNoSuchRecordException() {
        // Arrange
        val rideId = UUID.randomUUID()
        doReturn(Optional.empty<Ride>()).`when`(repository).findById(rideId)

        // Act and Assert
        assertThrows<NoSuchRecordException> { service.confirmRideStart(rideId) }

        // Verify
        verify(repository, times(1)).findById(rideId)
    }

    @Test
    fun confirmRideEnd_rideCantBeEnded_shouldThrowRideEndConfirmationException() {
        // Arrange
        val rideId = UUID.randomUUID()
        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.REQUESTED
        )
            .id(rideId)
            .build()

        doReturn(Optional.of(ride)).`when`(repository).findById(rideId)
        doReturn(false).`when`(repository).canEndRide(rideId)

        // Act and Assert
        assertThrows<RideEndConfirmationException> { service.confirmRideEnd(rideId) }

        // Verify
        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).canEndRide(rideId)
    }


    @Test
    fun ratePassenger_correctRequest_PassengerRated() {
        // Arrange
        val request = createDefaultSendRatingRequest()
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
            raterId = ride.driverProfileId!!,
            ratedId = ride.passengerProfileId,
            rating = request.rating
        )

        doReturn(Optional.of(ride)).`when`(repository).findById(rideId)

        // Act
        service.ratePassenger(request)

        // Assert
        verify(repository, times(1)).findById(rideId)
        verify(producerService, times(1)).ratePassenger(expectedSendRatingModel)
    }

    @Test
    fun ratePassenger_incorrectRideStatus_shouldThrowSendRatingException() {
        // Arrange
        val request = createDefaultSendRatingRequest()
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

        doReturn(Optional.of(ride)).`when`(repository).findById(rideId)

        // Act and Assert
        assertThrows<SendRatingException> { service.ratePassenger(request) }

        // Verify
        verify(repository, times(1)).findById(rideId)
    }

    @Test
    fun ratePassenger_notAllowedTimeAfterRideCompleted_shouldThrowSendRatingException() {
        // Arrange
        val request = createDefaultSendRatingRequest()
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

        doReturn(Optional.of(ride)).`when`(repository).findById(rideId)

        // Act and Assert
        assertThrows<SendRatingException> { service.ratePassenger(request) }

        // Verify
        verify(repository, times(1)).findById(rideId)
    }


    @Test
    fun confirmDriverRated_correctRequest_confirmDriverRated() {
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
            .driverRating(model.rating)
            .build()

        doReturn(Optional.of(ride)).`when`(repository).findById(rideId)

        // Act
        service.confirmDriverRated(model = model)

        // Assert
        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).save(expectedRide)
    }

    @Test
    fun updateDriverPosition_correctUpdateRequest_updateDriverPosition() {
        // Arrange
        val request = createDefaultUpdatePositionRequest()
        val rideId = request.rideId

        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.COMPLETED
        )
            .id(rideId)
            .driverProfileId(UUID.randomUUID())
            .passengerPosition(mapper.fromRequestPointToPoint(request.location))
            .build()

        doReturn(Optional.of(ride)).`when`(repository).findById(rideId)

        val expectedSaveRide = Ride.builder(
            passengerProfileId = ride.passengerProfileId,
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.COMPLETED
        )
            .id(rideId)
            .driverProfileId(ride.driverProfileId)
            .driverPosition(mapper.fromRequestPointToPoint(request.location))
            .passengerPosition(mapper.fromRequestPointToPoint(request.location))
            .build()

        val expectedResult = UpdatePositionResponse(
            rideId = rideId,
            userPosition = mapper.fromPointToResponsePoint(ride.passengerPosition),
            status = RideStatus.COMPLETED,
        )

        // Act
        val result = service.updateDriverPosition(request)

        // Assert
        assertEquals(expectedResult, result)
        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).save(expectedSaveRide)
    }

    @Test
    fun getAllRides_correctRequest_returnRidePage() {
        // Arrange
        val driverId = UUID.randomUUID()
        val request = createDefaultRidePageRequest()
        val pageRequest = PageRequest.of(request.pageNumber - 1, request.pageSize, Sort.by(request.orderBy))
        val page = PageImpl<Ride>(listOf())
        doReturn(page).`when`(repository).getRidesByDriverProfileId(driverId, pageRequest)
        val expectedResult = RidePageResponse(
            profiles = listOf(),
            pageNumber = 1,
            totalElements = 0,
            totalPages = 1
        )

        // Act
        val result = service.getAllRides(driverId, request)

        // Assert
        assertEquals(expectedResult, result)
        verify(repository, times(1)).getRidesByDriverProfileId(driverId, pageRequest)
    }

}