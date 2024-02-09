package io.voitovich.yura.rideservice.integration.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.dto.request.AcceptRideRequest
import io.voitovich.yura.rideservice.dto.request.GetAvailableRidesRequest
import io.voitovich.yura.rideservice.dto.request.RequestPoint
import io.voitovich.yura.rideservice.dto.request.SendRatingRequest
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.exceptionhandler.model.ExceptionInfo
import io.voitovich.yura.rideservice.integration.util.DriverManagementIntegrationTestsUtils.Companion.getAllDriverRides
import io.voitovich.yura.rideservice.integration.util.DriverManagementIntegrationTestsUtils.Companion.getAllDriverRidesDriverProfileModel
import io.voitovich.yura.rideservice.integration.util.DriverManagementIntegrationTestsUtils.Companion.getAllDriverRidesPassengerProfileModels
import io.voitovich.yura.rideservice.integration.util.DriverManagementIntegrationTestsUtils.Companion.getRidesWithRadius600
import io.voitovich.yura.rideservice.integration.util.DriverManagementIntegrationTestsUtils.Companion.getRidesWithRadius700
import io.voitovich.yura.rideservice.integration.util.Utils.Companion.executeRequest
import io.voitovich.yura.rideservice.integration.util.Utils.Companion.setupDriverWireMock
import io.voitovich.yura.rideservice.integration.util.Utils.Companion.setupPassengersWireMock
import io.voitovich.yura.rideservice.properties.DefaultApplicationProperties
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.NOT_VALID_SEARCH_RADIUS_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.NO_SUCH_RECORD_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.RATE_PASSENGER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.RIDE_ALREADY_ACCEPTED_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.RIDE_END_INVALID_DRIVER_LOCATION_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.RIDE_END_INVALID_STATUS_EXCEPTION_MESSAGE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.util.*

class RideDriverManagementServiceIntegrationTest: AbstractControllerIntegrationTest() {

    @Autowired
    lateinit var properties: DefaultApplicationProperties

    @Qualifier("mockDriverServiceDriverServiceManagement")
    @Autowired
    lateinit var driverWireMock: WireMockServer

    @Qualifier("mockPassengerServiceDriverServiceManagement")
    @Autowired
    lateinit var passengerWireMock: WireMockServer

    companion object {

        private const val DRIVER_MANAGEMENT_CONTROLLER_BASE_URL = "api/ride/driver"

    }


    @Nested
    @DisplayName("Confirm ride start")
    inner class ConfirmRideStartTests {

        @Test
        fun confirmRideStart_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {
            // Arrange
            val randomId = UUID.randomUUID().toString()
            val expected = ExceptionInfo(HttpStatus.NOT_FOUND, String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, randomId))

            // Act
            val actual = executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-start/{id}",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.NOT_FOUND,
                pathParamName = "id",
                pathParam = randomId,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun confirmRideStart_driverLocationIsNotValid_shouldReturnRideStartConfirmationErrorResponse() {
            // Arrange
            val expected = ExceptionInfo(
                HttpStatus.BAD_REQUEST,
                RideDriverManagementServiceImpl.RIDE_START_INVALID_DRIVER_LOCATION_EXCEPTION_MESSAGE
            )

            // Act
            val actual = executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-start/{id}",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.BAD_REQUEST,
                pathParamName = "id",
                pathParam = "4ba65be8-cd97-4d40-aeae-8eb5a71fa50c",
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun confirmRideStart_notValidRideStatus_shouldReturnRideStartConfirmationErrorResponse() {
            // Arrange
            val expected = ExceptionInfo(
                HttpStatus.BAD_REQUEST,
                RideDriverManagementServiceImpl.RIDE_START_INVALID_STATUS_EXCEPTION_MESSAGE
            )

            // Act
            val actual = executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-start/{id}",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.BAD_REQUEST,
                pathParamName = "id",
                pathParam = "4ba65be8-cd97-4d40-aeae-8eb5a71fa51c",
                extractClass = ExceptionInfo::class.java,
                token = userToken,
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun confirmRideStart_validRideCondition_shouldConfirmRideStart() {
            // Act
            executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-start/{id}",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.OK,
                pathParamName = "id",
                token = userToken,
                pathParam = "4ba65be8-cd97-4d40-aeae-8eb5a71fa52c"
            )

        }
    }


    @Nested
    @DisplayName("Get available rides")
    inner class GetAvailableRidesTests {

        @Test
        fun getAvailableRides_notValidSearchRadius_shouldReturnNotValidSearchRadiusErrorResponse() {
            // Arrange
            properties.useDefaultRadiusIfRadiusNotInRange = false
            val request = GetAvailableRidesRequest(
                id = UUID.randomUUID(),
                currentLocation = RequestPoint(
                    BigDecimal.valueOf(40.7128),
                    BigDecimal.valueOf(40.7128)
                ),
                radius = 1500
            )
            val expected = ExceptionInfo(
                HttpStatus.BAD_REQUEST,
                String.format(NOT_VALID_SEARCH_RADIUS_EXCEPTION_MESSAGE, properties.minRadius, properties.maxRadius)
            )

            // Act
            val actual = executeRequest(
                port = port,
                url = DRIVER_MANAGEMENT_CONTROLLER_BASE_URL,
                method = HttpMethod.GET,
                expectedStatus = HttpStatus.BAD_REQUEST,
                body = request,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun getAvailableRides_notValidSearchRadius_shouldUseDefaultSearchRadius() {
            // Arrange
            properties.useDefaultRadiusIfRadiusNotInRange = true
            val request = GetAvailableRidesRequest(
                id = UUID.randomUUID(),
                currentLocation = RequestPoint(
                    BigDecimal.valueOf(40.1128),
                    BigDecimal.valueOf(-74.003)
                ),
                radius = 1500
            )
            val expected = GetAvailableRidesResponse(
                getRidesWithRadius700()
            )

            // Act
            val actual = executeRequest(
                port = port,
                url = DRIVER_MANAGEMENT_CONTROLLER_BASE_URL,
                method = HttpMethod.GET,
                expectedStatus = HttpStatus.OK,
                body = request,
                token = userToken,
                extractClass = GetAvailableRidesResponse::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun getAvailableRides_validSearchRadius_shouldReturnOneRide() {
            // Arrange
            properties.useDefaultRadiusIfRadiusNotInRange = false
            val request = GetAvailableRidesRequest(
                id = UUID.randomUUID(),
                currentLocation = RequestPoint(
                    BigDecimal.valueOf(40.1128),
                    BigDecimal.valueOf(-74.003)
                ),
                radius = 600
            )
            val expected = GetAvailableRidesResponse(
                getRidesWithRadius600()
            )

            // Act
            val actual = executeRequest(
                port = port,
                url = DRIVER_MANAGEMENT_CONTROLLER_BASE_URL,
                method = HttpMethod.GET,
                expectedStatus = HttpStatus.OK,
                body = request,
                token = userToken,
                extractClass = GetAvailableRidesResponse::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }
    }


    @Nested
    @DisplayName("Accept ride")
    inner class AcceptRideTests {

        @Test
        fun acceptRide_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {
            // Arrange
            val driverId = UUID.randomUUID()
            val driverProfileModel = getDriverProfileModel(driverId)
            setupDriverWireMock(driverWireMock, driverId.toString(), HttpStatus.OK, driverProfileModel)

            val randomId = UUID.randomUUID().toString()
            val request = getAcceptRideRequest(driverId, randomId)

            val expected =
                ExceptionInfo(HttpStatus.NOT_FOUND, String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, randomId))

            // Act
            val actual = executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/accept",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.NOT_FOUND,
                body = request,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun acceptRide_invalidRideStatus_shouldReturnRideAlreadyAcceptedErrorResponse() {
            // Arrange
            val driverId = UUID.randomUUID()
            val driverProfileModel = getDriverProfileModel(driverId)
            setupDriverWireMock(driverWireMock, driverId.toString(), HttpStatus.OK, driverProfileModel)

            val rideId = "4ba65be8-cd97-4d40-aeae-8eb5a71fa55c"
            val request = getAcceptRideRequest(driverId, rideId)

            val expected =
                ExceptionInfo(HttpStatus.CONFLICT, String.format(RIDE_ALREADY_ACCEPTED_EXCEPTION_MESSAGE, rideId))

            // Act
            val actual = executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/accept",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.CONFLICT,
                body = request,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun acceptRide_correctRequest_shouldAcceptRide() {
            // Arrange
            val driverId = UUID.randomUUID()
            val driverProfileModel = getDriverProfileModel(driverId)
            setupDriverWireMock(driverWireMock, driverId.toString(), HttpStatus.OK, driverProfileModel)

            val rideId = "4ba65be8-cd97-4d40-aeae-8eb5a71fa53c"
            val request = getAcceptRideRequest(driverId, rideId)

            // Act
            executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/accept",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.OK,
                token = userToken,
                body = request,
            )

        }

        @Test
        fun acceptRide_driverNotExists_shouldReturnNoSuchRecordErrorResponse() {
            // Arrange
            val driverId = UUID.randomUUID()
            val driverProfileModel = getDriverProfileModel(driverId)
            setupDriverWireMock(driverWireMock, driverId.toString(), HttpStatus.NOT_FOUND, driverProfileModel)

            val randomId = UUID.randomUUID().toString()
            val request = getAcceptRideRequest(driverId, randomId)

            val expected = ExceptionInfo(HttpStatus.NOT_FOUND, "Driver profile was not found")

            // Act
            val actual = executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/accept",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.NOT_FOUND,
                body = request,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        private fun getDriverProfileModel(driverId: UUID): String {
            val driverProfileModel = DriverProfileModel(
                id = driverId,
                phoneNumber = "+375295432551",
                name = "Name",
                surname = "Surname",
                rating = BigDecimal.ONE,
                experience = 3
            )
            return jacksonObjectMapper().writeValueAsString(driverProfileModel)
        }

        private fun getAcceptRideRequest(driverId: UUID, rideId: String): AcceptRideRequest {
            return AcceptRideRequest(
                rideId = UUID.fromString(rideId),
                driverId = driverId,
                RequestPoint(
                    BigDecimal.valueOf(5),
                    BigDecimal.valueOf(5)
                )
            )
        }
    }

    @Nested
    @DisplayName("Confirm ride end")
    inner class ConfirmRideEndTest {

        @Test
        fun confirmRideEnd_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {
            // Arrange
            val randomId = UUID.randomUUID().toString()
            val expected = ExceptionInfo(HttpStatus.NOT_FOUND, String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, randomId))

            // Act
            val actual = executeConfirmRideEndRequest(randomId)

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun confirmRideEnd_driverLocationIsNotValid_shouldReturnRideEndConfirmationErrorResponse() {
            // Arrange
            val expected = ExceptionInfo(HttpStatus.BAD_REQUEST, RIDE_END_INVALID_DRIVER_LOCATION_EXCEPTION_MESSAGE)

            val actual = executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-end/{id}",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.BAD_REQUEST,
                pathParamName = "id",
                pathParam = "4ba65be8-cd97-4d40-aeae-8eb5a71fa56c",
                extractClass = ExceptionInfo::class.java,
                token = userToken,
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun confirmRideEnd_notValidRideStatus_shouldReturnRideEndConfirmationErrorResponse() {
            // Arrange
            val expected = ExceptionInfo(HttpStatus.BAD_REQUEST, RIDE_END_INVALID_STATUS_EXCEPTION_MESSAGE)

            // Act
            val actual = executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-end/{id}",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.BAD_REQUEST,
                pathParamName = "id",
                token = userToken,
                pathParam = "4ba65be8-cd97-4d40-aeae-8eb5a71fa57c",
                extractClass = ExceptionInfo::class.java
            )

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun confirmRideEnd_validRideCondition_shouldConfirmRideEnd() {
            // Act
            executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-end/{id}",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.OK,
                pathParamName = "id",
                token = userToken,
                pathParam = "4ba65be8-cd97-4d40-aeae-8eb5a71fa58c",
            )
        }

        private fun executeConfirmRideEndRequest(rideId: String): ExceptionInfo {
            return executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-end/{id}",
                method = HttpMethod.POST,
                expectedStatus = HttpStatus.NOT_FOUND,
                pathParamName = "id",
                pathParam = rideId,
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )
        }
    }


    @Nested
    @DisplayName("Get all driver rides")
    inner class GetAllRidesTest {
        @Test
        fun getAllRides_validRequest_shouldReturnAllRidesForDriverWithId() {
            // Arrange
            val allDriverRides = getAllDriverRides()
            val expected = RidePageResponse(
                profiles = allDriverRides,
                pageNumber = 1,
                totalElements = 3,
                totalPages = 2
            )
            val passengerIds = listOf("7e4f5342-cb2b-4e8c-8ab7-1629afcf5d01", "7e4f5342-cb2b-4e8c-8ab7-1629afcf5d02")
            val driverId = "d1e42c0d-aa71-448c-843f-5a5e801ed287"
            val modelsJson = jacksonObjectMapper().writeValueAsString(getAllDriverRidesPassengerProfileModels())
            val driverProfileModel = jacksonObjectMapper().writeValueAsString(getAllDriverRidesDriverProfileModel())

            setupPassengersWireMock(
                passengerWireMock,
                passengerIds,
                HttpStatus.OK,
                modelsJson
            )

            setupDriverWireMock(
                driverWireMock = driverWireMock,
                id = driverId,
                responseStatus = HttpStatus.OK,
                responseBody = driverProfileModel
            )

            // Act
            val actual = executeGetAllRidesRequest(driverId)

            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun getAllRides_badParams_shouldReturnConstraintViolationErrorResponse() {
            // Arrange
            val driverId = "d1e42c0d-aa71-448c-843f-5a5e801ed287"

            // Act
            val actual = executeGetAllRidesRequestWithBadParams(driverId)

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, actual.status)
        }

        private fun executeGetAllRidesRequest(driverId: String): RidePageResponse {
            return executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/rides/{id}",
                method = HttpMethod.GET,
                expectedStatus = HttpStatus.OK,
                pathParamName = "id",
                pathParam = driverId,
                params = mapOf(
                    "pageNumber" to 1,
                    "pageSize" to 2,
                    "orderBy" to "id"
                ),
                token = userToken,
                extractClass = RidePageResponse::class.java
            )
        }

        private fun executeGetAllRidesRequestWithBadParams(driverId: String): ExceptionInfo {
            return executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/rides/{id}",
                method = HttpMethod.GET,
                expectedStatus = HttpStatus.BAD_REQUEST,
                pathParam = driverId,
                pathParamName = "id",
                params = mapOf(
                    "pageNumber" to 0,
                    "pageSize" to 0,
                    "orderBy" to "ids"
                ),
                token = userToken,
                extractClass = ExceptionInfo::class.java
            )
        }
    }

    @Nested
    @DisplayName("Rate passenger")
    inner class RatePassengerTests {

        @Test
        fun ratePassenger_correctRequest_shouldRatePassenger() {
            // Arrange
            val ratePassengerRequest = SendRatingRequest(
                rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa65c"),
                rating = BigDecimal.ONE
            )

            // Act
            executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/rate",
                method = HttpMethod.POST,
                body = ratePassengerRequest,
                expectedStatus = HttpStatus.OK,
                token = userToken,
            )

        }

        @Test
        fun ratePassenger_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {
            // Arrange
            val ratePassengerRequest = SendRatingRequest(
                rideId = UUID.randomUUID(),
                rating = BigDecimal.ONE
            )
            val expected = ExceptionInfo(
                HttpStatus.NOT_FOUND,
                message = String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, ratePassengerRequest.rideId)
            )

            // Act
            val actual = executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/rate",
                method = HttpMethod.POST,
                body = ratePassengerRequest,
                expectedStatus = HttpStatus.NOT_FOUND,
                extractClass = ExceptionInfo::class.java,
                token = userToken,
            )


            // Assert
            assertEquals(expected, actual)
        }

        @Test
        fun ratePassenger_rideStatusIsNotValid_shouldReturnSendRatingErrorResponse() {
            // Arrange
            val ratePassengerRequest = SendRatingRequest(
                rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa62c"),
                rating = BigDecimal.ONE
            )
            val expected = ExceptionInfo(
                HttpStatus.BAD_REQUEST,
                message = RATE_PASSENGER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE
            )

            // Act
            val actual = executeRequest(
                port = port,
                url = "$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/rate",
                method = HttpMethod.POST,
                body = ratePassengerRequest,
                expectedStatus = HttpStatus.BAD_REQUEST,
                extractClass = ExceptionInfo::class.java,
                token = userToken,
            )

            // Assert
            assertEquals(expected, actual)
        }
    }


}