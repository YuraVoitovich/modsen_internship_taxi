package io.voitovich.yura.rideservice.unit

import io.voitovich.yura.rideservice.client.DriverServiceClient
import io.voitovich.yura.rideservice.client.PassengerServiceClient
import io.voitovich.yura.rideservice.client.service.DriverClientService
import io.voitovich.yura.rideservice.client.service.PassengerClientService
import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.mapper.impl.RideMapperImpl
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.dto.responce.UpdatePositionResponse
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.event.model.SendRatingModel
import io.voitovich.yura.rideservice.event.service.KafkaProducerService
import io.voitovich.yura.rideservice.exception.*
import io.voitovich.yura.rideservice.model.RideProjection
import io.voitovich.yura.rideservice.properties.DefaultApplicationProperties
import io.voitovich.yura.rideservice.properties.DefaultKafkaProperties
import io.voitovich.yura.rideservice.repository.RideRepository
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultAcceptRideRequest
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultConfirmRatingReceiveModel
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultGetAvailableRidesRequest
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultGetAvailableRidesRequestWithInvalidRadius
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultGetAvailableRidesRequestWithoutRadius
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultPoint
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultRidePageRequest
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultSendRatingRequest
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultUpdatePositionRequest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.locationtech.jts.geom.Point
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.TestPropertySource
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Optional
import java.util.UUID


@TestPropertySource(locations = ["classpath:application-test.yml"])
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

    private var closeable: AutoCloseable? = null

    private lateinit var clock: Clock;

    @BeforeEach
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
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

    @AfterEach
    @Throws(Exception::class)
    fun tearDown() {
        closeable!!.close()
    }

    @Test
    fun getAvailableRides_correctRequest_shouldReturnRide() {
        val getAvailableRidesRequest = createDefaultGetAvailableRidesRequest()
        val radius: Int = getAvailableRidesRequest.radius!!
        val point = mapper.fromRequestPointToPoint(getAvailableRidesRequest.currentLocation)
        doReturn(listOf<RideProjection>()).`when`(repository)
            .getDriverAvailableRides(point, radius)

        val response = service.getAvailableRides(getAvailableRidesRequest)

        verify(repository, times(1)).getDriverAvailableRides(point, radius = radius)
    }

    @Test
    fun getAvailableRides_correctRequestWithoutRadius_shouldReturnRide() {
        val getAvailableRidesRequest = createDefaultGetAvailableRidesRequestWithoutRadius()
        val point = mapper.fromRequestPointToPoint(getAvailableRidesRequest.currentLocation)
        doReturn(listOf<RideProjection>()).`when`(repository)
            .getDriverAvailableRides(point, properties.searchRadius)

        val response = service.getAvailableRides(getAvailableRidesRequest)

        verify(repository, times(1)).getDriverAvailableRides(point, properties.searchRadius)
    }

    @Test
    fun getAvailableRides_incorrectRadiusUseDefaultRadiusFalse_shouldThrowNotValidSearchRadiusException() {
        val getAvailableRidesRequest = createDefaultGetAvailableRidesRequestWithInvalidRadius()

        assertThrows<NotValidSearchRadiusException> { service.getAvailableRides(getAvailableRidesRequest) }

    }

    @Test
    fun getAvailableRides_incorrectRadiusUseDefaultRadiusTrue_shouldThrowNotValidSearchRadiusException() {
        val getAvailableRidesRequest = createDefaultGetAvailableRidesRequestWithInvalidRadius()
        properties.useDefaultRadiusIfRadiusNotInRange = true
        val radius = 500
        val point = mapper.fromRequestPointToPoint(getAvailableRidesRequest.currentLocation)
        doReturn(listOf<RideProjection>()).`when`(repository)
            .getDriverAvailableRides(point, radius)

        val response = service.getAvailableRides(getAvailableRidesRequest)

        verify(repository, times(1)).getDriverAvailableRides(point, radius)
    }

    @Test
    fun acceptRide_correctRequest_shouldAcceptRide() {
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


        service.acceptRide(acceptRideRequest)

        verify(repository, times(1)).findById(acceptRideRequest.rideId)
        verify(repository, times(1)).save(expectedRide)

    }


    @Test
    fun acceptRide_wrongRideStatus_shouldThrowRideAlreadyAcceptedException() {
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

        assertThrows<RideAlreadyAcceptedException> { service.acceptRide(acceptRideRequest) }

        verify(repository, times(1)).findById(acceptRideRequest.rideId)

    }

    @Test
    fun acceptRide_rideNotFound_shouldThrowNoSuchRecordException() {
        val acceptRideRequest = createDefaultAcceptRideRequest()

        doReturn(Optional.empty<Ride>()).`when`(repository)
            .findById(acceptRideRequest.rideId)

        assertThrows<NoSuchRecordException> { service.acceptRide(acceptRideRequest) }

        verify(repository, times(1)).findById(acceptRideRequest.rideId)

    }

    @Test
    fun confirmRideEnd_correctRequest_shouldConfirmRideEnd() {
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

        service.confirmRideEnd(rideId)

        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).canEndRide(rideId)
        verify(repository, times(1)).save(expectedRide)

    }


    @Test
    fun confirmRideEnd_rideNotFound_shouldThrowNoSuchRecordException() {
        val rideId = UUID.randomUUID()

        doReturn(Optional.empty<Ride>()).`when`(repository)
            .findById(rideId)

        assertThrows<NoSuchRecordException> { service.confirmRideEnd(rideId) }

        verify(repository, times(1)).findById(rideId)

    }

    @Test
    fun confirmRideStart_rideCantBeStarted_shouldThrowRideStartConfirmationException() {
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


        assertThrows<RideStartConfirmationException> { service.confirmRideStart(rideId) }

        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).canStartRide(rideId)

    }



    @Test
    fun confirmRideStart_correctRequest_shouldConfirmRideStart() {
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


        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        doReturn(true).`when`(repository).canStartRide(rideId)

        service.confirmRideStart(rideId)

        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).canStartRide(rideId)
        verify(repository, times(1)).save(expectedRide)

    }


    @Test
    fun confirmRideStart_rideNotFound_shouldThrowNoSuchRecordException() {
        val rideId = UUID.randomUUID()

        doReturn(Optional.empty<Ride>()).`when`(repository)
            .findById(rideId)

        doReturn(true).`when`(repository).canStartRide(rideId)


        assertThrows<NoSuchRecordException> { service.confirmRideStart(rideId) }

        verify(repository, times(1)).findById(rideId)

    }

    @Test
    fun confirmRideEnd_rideCantBeEnded_shouldThrowRideEndConfirmationException() {
        val rideId = UUID.randomUUID()
        val ride = Ride.builder(
            passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.REQUESTED
        )
        .id(rideId)
        .build()

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        doReturn(false).`when`(repository).canEndRide(rideId)


        assertThrows<RideEndConfirmationException> { service.confirmRideEnd(rideId) }

        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).canEndRide(rideId)

    }

    @Test
    fun ratePassenger_correctRequest_PassengerRated() {
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

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        service.ratePassenger(request)

        verify(repository, times(1)).findById(rideId)
        verify(producerService, times(1)).ratePassenger(expectedSendRatingModel)


    }


    @Test
    fun ratePassenger_incorrectRideStatus_shouldThrowSendRatingException() {
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


        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        assertThrows<SendRatingException> { service.ratePassenger(request) }

        verify(repository, times(1)).findById(rideId)


    }


    @Test
    fun ratePassenger_notAllowedTimeAfterRideCompleted_shouldThrowSendRatingException() {
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


        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        assertThrows<SendRatingException> { service.ratePassenger(request) }

        verify(repository, times(1)).findById(rideId)


    }

    @Test
    fun confirmDriverRated_correctRequest_confirmDriverRated() {

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


        doReturn(Optional.of(ride)).`when`(repository)
            .findById(rideId)

        service.confirmDriverRated(model = model)

        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).save(expectedRide)

    }


    @Test
    fun updateDriverPosition_correctUpdateRequest_updateDriverPosition() {
        val request = createDefaultUpdatePositionRequest()
        val rideId = request.rideId

        val ride = Ride.builder(passengerProfileId = UUID.randomUUID(),
            startPoint = createDefaultPoint(mapper),
            endPoint = createDefaultPoint(mapper),
            status = RideStatus.COMPLETED)
            .id(rideId)
            .driverProfileId(UUID.randomUUID())
            .passengerPosition(mapper.fromRequestPointToPoint(request.location))
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
            userPosition = mapper.fromPointToResponsePoint(ride.passengerPosition),
            status = RideStatus.COMPLETED,
        )


        val result = service.updateDriverPosition(request)


        assertEquals(expectedResult, result)
        verify(repository, times(1)).findById(rideId)
        verify(repository, times(1)).save(expectedSaveRide)
    }

    @Test
    fun getAllRides_correctRequest_returnRidePage() {
        val driverId = UUID.randomUUID()
        val request = createDefaultRidePageRequest()
        val pageRequest = PageRequest.of(request.pageNumber - 1, request.pageSize, Sort.by(request.orderBy))
        val page = PageImpl<Ride>(listOf())
        doReturn(page).`when`(repository).getRidesByDriverProfileId(driverId, pageRequest)
        val expectedResult = RidePageResponse(
            listOf(),
            1,
            0,
            1)


        val result = service.getAllRides(driverId, request)


        assertEquals(expectedResult, result)
        verify(repository, times(1)).getRidesByDriverProfileId(driverId, pageRequest)
    }

}