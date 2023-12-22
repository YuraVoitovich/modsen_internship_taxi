package io.voitovich.yura.rideservice.unit

import io.voitovich.yura.rideservice.client.service.DriverClientService
import io.voitovich.yura.rideservice.client.service.PassengerClientService
import io.voitovich.yura.rideservice.dto.mapper.RideMapper
import io.voitovich.yura.rideservice.dto.mapper.impl.RideMapperImpl
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.entity.Ride
import io.voitovich.yura.rideservice.entity.RideStatus
import io.voitovich.yura.rideservice.exception.NoSuchRecordException
import io.voitovich.yura.rideservice.properties.DefaultApplicationProperties
import io.voitovich.yura.rideservice.repository.RideRepository
import io.voitovich.yura.rideservice.service.impl.RidePassengerManagementServiceImpl
import io.voitovich.yura.rideservice.service.impl.RideServiceImpl
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils
import io.voitovich.yura.rideservice.unit.util.UnitTestsUtils.Companion.createDefaultPassengerProfileModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.*

class RideServiceImplTest {

    private lateinit var mapper: RideMapper
    @Mock
    private lateinit var repository: RideRepository

    @Mock
    private lateinit var driverClientService: DriverClientService

    @Mock
    private lateinit var passengerClientService: PassengerClientService

    private var closeable: AutoCloseable? = null

    private lateinit var service: RideServiceImpl

    @BeforeEach
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)

        mapper = RideMapperImpl(driverClientService, passengerClientService)

        service = RideServiceImpl(
            repository = repository,
            mapper = mapper,
        )
    }

    @AfterEach
    @Throws(Exception::class)
    fun tearDown() {
        closeable!!.close()
    }

    @Test
    fun getRideById_rideExists_shouldReturnRide() {
        val id = UUID.randomUUID()

        val passengerModel = createDefaultPassengerProfileModel()
        doReturn(passengerModel).`when`(passengerClientService).getPassengerProfile(passengerModel.id)
        val ride = Ride.builder(
            passengerProfileId = passengerModel.id,
            startPoint = UnitTestsUtils.createDefaultPoint(mapper),
            endPoint = UnitTestsUtils.createDefaultPoint(mapper),
            status = RideStatus.REQUESTED
        ).id(id).build()

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(id)

        service.getRideById(id)

        verify(repository, Mockito.times(1)).findById(id)
    }

    @Test
    fun getRideById_rideNotExists_shouldThrowNoSuchRecordException() {
        val id = UUID.randomUUID()

        val passengerModel = createDefaultPassengerProfileModel()
        doReturn(passengerModel).`when`(passengerClientService).getPassengerProfile(passengerModel.id)

        doReturn(Optional.empty<Ride>()).`when`(repository)
            .findById(id)

        assertThrows<NoSuchRecordException>{ service.getRideById(id) }

        verify(repository, Mockito.times(1)).findById(id)
    }

    @Test
    fun deleteRideById_rideExists_shouldDeleteRide() {
        val id = UUID.randomUUID()

        val ride = Ride.builder(
            passengerProfileId = id,
            startPoint = UnitTestsUtils.createDefaultPoint(mapper),
            endPoint = UnitTestsUtils.createDefaultPoint(mapper),
            status = RideStatus.REQUESTED
        ).id(id).build()

        doReturn(Optional.of(ride)).`when`(repository)
            .findById(id)

        service.deleteRideById(id)

        verify(repository, Mockito.times(1)).findById(id)
        verify(repository, Mockito.times(1)).deleteById(id)
    }

    @Test
    fun deleteRideById_rideNotExists_shouldThrowNoSuchRecordException() {
        val id = UUID.randomUUID()

        doReturn(Optional.empty<Ride>()).`when`(repository)
            .findById(id)

        assertThrows<NoSuchRecordException> { service.deleteRideById(id) }

        verify(repository, Mockito.times(1)).findById(id)
    }

    @Test
    fun getRidePage_correctRequest_returnRidePage() {
        val driverId = UUID.randomUUID()
        val request = UnitTestsUtils.createDefaultRidePageRequest()
        val pageRequest = PageRequest.of(request.pageNumber - 1, request.pageSize, Sort.by(request.orderBy))
        val page = PageImpl<Ride>(listOf())
        doReturn(page).`when`(repository).findAll(pageRequest)
        val expectedResult = RidePageResponse(
            listOf(),
            1,
            0,
            1)


        val result = service.getRidePage(request)


        assertEquals(expectedResult, result)
        verify(repository, Mockito.times(1)).findAll(pageRequest)
    }
}