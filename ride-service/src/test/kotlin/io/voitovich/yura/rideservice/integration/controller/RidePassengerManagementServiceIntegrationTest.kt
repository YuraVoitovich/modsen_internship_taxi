package io.voitovich.yura.rideservice.integration.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import io.voitovich.yura.rideservice.dto.request.CancelRequest
import io.voitovich.yura.rideservice.dto.request.SendRatingRequest
import io.voitovich.yura.rideservice.dto.responce.CreateRideResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.exceptionhandler.model.ExceptionInfo
import io.voitovich.yura.rideservice.integration.config.WireMockConfig
import io.voitovich.yura.rideservice.integration.util.PassengerManagementIntegrationTestsUtils.Companion.getAllPassengerRides
import io.voitovich.yura.rideservice.integration.util.PassengerManagementIntegrationTestsUtils.Companion.getAllPassengerRidesDriverProfilesModels
import io.voitovich.yura.rideservice.integration.util.PassengerManagementIntegrationTestsUtils.Companion.getAllPassengerRidesPassengerProfileModel
import io.voitovich.yura.rideservice.integration.util.PassengerManagementIntegrationTestsUtils.Companion.getDefaultCreateRideRequest
import io.voitovich.yura.rideservice.integration.util.RideManagementIntegrationTestsUtils.Companion.createDefaultPassengerProfileModel
import io.voitovich.yura.rideservice.integration.util.Utils.Companion.executeRequest
import io.voitovich.yura.rideservice.integration.util.Utils.Companion.setupDriversWireMock
import io.voitovich.yura.rideservice.integration.util.Utils.Companion.setupPassengerWireMock
import io.voitovich.yura.rideservice.service.impl.RidePassengerManagementServiceImpl.Companion.NO_SUCH_RECORD_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RidePassengerManagementServiceImpl.Companion.RATE_DRIVER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RidePassengerManagementServiceImpl.Companion.RIDE_CANT_BE_CANCELED_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RidePassengerManagementServiceImpl.Companion.RIDE_CANT_BE_STARTED_EXCEPTION_MESSAGE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgisContainerProvider
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import java.util.*

class RidePassengerManagementServiceIntegrationTest: AbstractControllerIntegrationTest() {

    @Qualifier("mockDriverServiceDriverServiceManagement")
    @Autowired
    lateinit var driverWireMock: WireMockServer

    @Qualifier("mockPassengerServiceDriverServiceManagement")
    @Autowired
    lateinit var passengerWireMock: WireMockServer

    companion object {
        private const val PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL = "api/ride/passenger"
    }

    @Nested
    @DisplayName("Create ride tests")
    inner class CreateRideTests {
        @Test
        fun createRide_correctRequest_shouldReturnCreateRideResponse() {
            // Arrange
            val passengerId = UUID.randomUUID()
            val passengerProfileModel = createDefaultPassengerProfileModel(passengerId)
            val passengerProfileModelJSON = jacksonObjectMapper().writeValueAsString(passengerProfileModel)

            setupPassengerWireMock(
                passengerWireMock = passengerWireMock,
                passengerId = passengerId.toString(),
                responseStatus = HttpStatus.OK,
                responseBody = passengerProfileModelJSON
            )

            val request = getDefaultCreateRideRequest(passengerId)

            // Act
            val actual = executeRequest(
                port = port,
                url = PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL,
                method = HttpMethod.PUT,
                body = request,
                expectedStatus = HttpStatus.CREATED,
                token = userToken,
                extractClass = CreateRideResponse::class.java
            )

            // Assert
            assertEquals(passengerId, actual.passengerId)
        }

        @Test
        fun createRide_passengerProfileNotFound_shouldReturnNoSuchRecordErrorResponse() {
            // Arrange
            val passengerId = UUID.randomUUID()

            setupPassengerWireMock(
                passengerWireMock = passengerWireMock,
                passengerId = passengerId.toString(),
                responseStatus = HttpStatus.NOT_FOUND
            )

            val request = getDefaultCreateRideRequest(passengerId)

            // Act
            val expected = ExceptionInfo(
                status = HttpStatus.NOT_FOUND,
                message = "Passenger profile was not found"
            )
            val actual = executeRequest(
                port = port,
                url = PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL,
                method = HttpMethod.PUT,
                body = request,
                expectedStatus = HttpStatus.NOT_FOUND,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun createRide_rideForPassengerExists_shouldReturnRideCantBeStartedErrorResponse() {
            // Arrange
            val passengerId = UUID.fromString("7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10")
            val passengerProfileModel = createDefaultPassengerProfileModel(passengerId)
            val passengerProfileModelJSON = jacksonObjectMapper().writeValueAsString(passengerProfileModel)

            setupPassengerWireMock(
                passengerWireMock = passengerWireMock,
                passengerId = passengerId.toString(),
                responseStatus = HttpStatus.OK,
                responseBody = passengerProfileModelJSON
            )

            val request = getDefaultCreateRideRequest(passengerId)

            // Act
            val expected = ExceptionInfo(
                status = HttpStatus.CONFLICT,
                message = String.format(RIDE_CANT_BE_STARTED_EXCEPTION_MESSAGE, passengerId)
            )
            val actual = executeRequest(
                port = port,
                url = PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL,
                method = HttpMethod.PUT,
                body = request,
                expectedStatus = HttpStatus.CONFLICT,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }
    }


    @Nested
    @DisplayName("Cancel ride tests")
    inner class CancelRideTests {
        @Test
        fun cancelRide_rideCantBeCanceled_shouldReturnRideCantBeCanceledErrorResponse() {
            // Arrange
            val passengerId = UUID.fromString("7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10")
            val request = CancelRequest(
                passengerId = passengerId,
                rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa63c")
            )
            val expected = ExceptionInfo(
                status = HttpStatus.CONFLICT,
                message = String.format(RIDE_CANT_BE_CANCELED_EXCEPTION_MESSAGE, request.rideId)
            )

            // Act
            val actual = executeRequest(
                port = port,
                url = PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL,
                method = HttpMethod.DELETE,
                body = request,
                expectedStatus = HttpStatus.CONFLICT,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun cancelRide_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {
            // Arrange
            val passengerId = UUID.fromString("7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10")
            val request = CancelRequest(
                passengerId = passengerId,
                rideId = UUID.randomUUID()
            )
            val expected = ExceptionInfo(
                status = HttpStatus.NOT_FOUND,
                message = String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, request.rideId)
            )

            // Act
            val actual: ExceptionInfo = executeRequest(
                port = port,
                url = PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL,
                method = HttpMethod.DELETE,
                body = request,
                expectedStatus = HttpStatus.NOT_FOUND,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun cancelRide_correctRequest_shouldCancelRide() {
            // Arrange
            val passengerId = UUID.fromString("7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10")
            val request = CancelRequest(
                passengerId = passengerId,
                rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa62c")
            )

            // Act
            executeRequest(
                port = port,
                url = PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL,
                method = HttpMethod.DELETE,
                body = request,
                token = userToken,
                expectedStatus = HttpStatus.NO_CONTENT
            )

        }
    }

    @Nested
    @DisplayName("Get all passenger rides tests")
    inner class GetAllRidesTests {
        @Test
        fun getAllRides_validRequest_shouldReturnAllRidesForDriverWithId() {
            // Arrange
            val allPassengerRides = getAllPassengerRides()
            val expected = RidePageResponse(
                profiles = allPassengerRides,
                pageNumber = 1,
                totalElements = 3,
                totalPages = 2
            )
            val driverIds = listOf("d1e4120d-aa71-448c-843f-5a5e801ed287", "d1843c0d-aa71-448c-843f-5a5e801ed287")
            val passengerId = "7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10"
            val modelsJson = jacksonObjectMapper().writeValueAsString(getAllPassengerRidesDriverProfilesModels())
            setupDriversWireMock(
                driverWireMock = driverWireMock,
                ids = driverIds,
                responseStatus = HttpStatus.OK,
                responseBody = modelsJson
            )
            val passengerProfileModelJson = jacksonObjectMapper().writeValueAsString(getAllPassengerRidesPassengerProfileModel())
            setupPassengerWireMock(
                passengerWireMock = passengerWireMock,
                passengerId = passengerId,
                responseStatus = HttpStatus.OK,
                responseBody = passengerProfileModelJson
            )

            // Act
            val actual = executeRequest(
                port = port,
                url = "$PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL/rides/{id}",
                method = HttpMethod.GET,
                expectedStatus = HttpStatus.OK,
                pathParamName = "id",
                pathParam = passengerId,
                params = mapOf(
                    "pageNumber" to 1,
                    "pageSize" to 2,
                    "orderBy" to "id"
                ),
                token = userToken,
                extractClass = RidePageResponse::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun getAllRides_badParams_shouldReturnConstraintViolationErrorResponse() {
            // Arrange
            val passengerId = "7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10"

            // Act
            val actual = executeRequest(
                port = port,
                url = "$PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL/rides/{id}",
                method = HttpMethod.GET,
                expectedStatus = HttpStatus.BAD_REQUEST,
                pathParamName = "id",
                pathParam = passengerId,
                params = mapOf(
                    "pageNumber" to 0,
                    "pageSize" to 0,
                    "orderBy" to "ids"
                ),
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, actual.status)
        }
    }


    @Nested
    @DisplayName("Rate driver tests")
    inner class RateDriverTests {
        @Test
        fun rateDriver_correctRequest_shouldRateDriver() {
            // Arrange
            val rateDriverRequest = SendRatingRequest(
                rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa65c"),
                rating = BigDecimal.ONE
            )

            // Act
            executeRequest(
                port = port,
                url = "$PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL/rate",
                method = HttpMethod.POST,
                body = rateDriverRequest,
                token = userToken,
                expectedStatus = HttpStatus.OK
            )

            // No explicit Assert needed for this test case
        }

        @Test
        fun rateDriver_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {
            // Arrange
            val rateDriverRequest = SendRatingRequest(
                rideId = UUID.randomUUID(),
                rating = BigDecimal.ONE
            )

            val expected = ExceptionInfo(
                HttpStatus.NOT_FOUND,
                message = String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, rateDriverRequest.rideId)
            )

            // Act
            val actual: ExceptionInfo = executeRequest(
                port = port,
                url = "$PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL/rate",
                method = HttpMethod.POST,
                body = rateDriverRequest,
                expectedStatus = HttpStatus.NOT_FOUND,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun rateDriver_rideStatusIsNotValid_shouldReturnSendRatingErrorResponse() {
            // Arrange
            val rateDriverRequest = SendRatingRequest(
                rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa62c"),
                rating = BigDecimal.ONE
            )

            val expected = ExceptionInfo(
                HttpStatus.BAD_REQUEST,
                message = RATE_DRIVER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE
            )

            // Act
            val actual: ExceptionInfo = executeRequest(
                port = port,
                url = "$PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL/rate",
                method = HttpMethod.POST,
                body = rateDriverRequest,
                expectedStatus = HttpStatus.BAD_REQUEST,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }
    }




}