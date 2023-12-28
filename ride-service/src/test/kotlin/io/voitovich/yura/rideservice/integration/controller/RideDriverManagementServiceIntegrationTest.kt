package io.voitovich.yura.rideservice.integration.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.voitovich.yura.rideservice.client.model.DriverProfileModel
import io.voitovich.yura.rideservice.dto.request.AcceptRideRequest
import io.voitovich.yura.rideservice.dto.request.GetAvailableRidesRequest
import io.voitovich.yura.rideservice.dto.request.RequestPoint
import io.voitovich.yura.rideservice.dto.request.SendRatingRequest
import io.voitovich.yura.rideservice.dto.responce.GetAvailableRidesResponse
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.exceptionhandler.model.ExceptionInfo
import io.voitovich.yura.rideservice.integration.config.WireMockConfig
import io.voitovich.yura.rideservice.integration.util.DriverManagementIntegrationTestsUtils.Companion.getAllDriverRides
import io.voitovich.yura.rideservice.integration.util.DriverManagementIntegrationTestsUtils.Companion.getAllDriverRidesDriverProfileModel
import io.voitovich.yura.rideservice.integration.util.DriverManagementIntegrationTestsUtils.Companion.getAllDriverRidesPassengerProfileModels
import io.voitovich.yura.rideservice.integration.util.DriverManagementIntegrationTestsUtils.Companion.getRidesWithRadius600
import io.voitovich.yura.rideservice.integration.util.DriverManagementIntegrationTestsUtils.Companion.getRidesWithRadius700
import io.voitovich.yura.rideservice.properties.DefaultApplicationProperties
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.NOT_VALID_SEARCH_RADIUS_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.NO_SUCH_RECORD_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.RATE_PASSENGER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.RIDE_ALREADY_ACCEPTED_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.RIDE_END_INVALID_DRIVER_LOCATION_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RideDriverManagementServiceImpl.Companion.RIDE_END_INVALID_STATUS_EXCEPTION_MESSAGE
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@SqlGroup(
    Sql(
        scripts = ["classpath:sql/truncate-passenger-profile-table.sql"],
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    ),
    Sql(
        scripts = ["classpath:sql/insert-test-values-in-passenger-profile-table.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = [WireMockConfig::class])
@ActiveProfiles("test")
class RideDriverManagementServiceIntegrationTest {

    @Autowired
    lateinit var properties: DefaultApplicationProperties

    @LocalServerPort
    private val port: Int? = null

    @Qualifier("mockDriverServiceDriverServiceManagement")
    @Autowired
    lateinit var driverWireMock: WireMockServer

    @Qualifier("mockPassengerServiceDriverServiceManagement")
    @Autowired
    lateinit var passengerWireMock: WireMockServer

    companion object {

        @Container
        @JvmStatic
        val kafka = KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
        )



        @Container
        @JvmStatic
        private val postgres = PostgisContainerProvider()
            .newInstance()
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("postgres")



        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            postgres.start()
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            postgres.stop()
        }

        private const val DRIVER_MANAGEMENT_CONTROLLER_BASE_URL = "api/ride/driver"

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(propertyRegistry: DynamicPropertyRegistry) {
            propertyRegistry.add("spring.kafka.bootstrap-servers") { kafka.bootstrapServers }
            propertyRegistry.add("spring.datasource.url") { postgres.jdbcUrl }
            propertyRegistry.add("spring.datasource.username") { postgres.username }
            propertyRegistry.add("spring.datasource.password") { postgres.password }
        }
    }

    @Test
    fun confirmRideStart_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {

        val randomId = UUID.randomUUID().toString()
        val expected = ExceptionInfo(HttpStatus.NOT_FOUND, String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, randomId))
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", randomId)
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-start/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }

    @Test
    fun confirmRideStart_driverLocationIsNotValid_shouldReturnRideStartConfirmationErrorResponse() {

        val expected = ExceptionInfo(HttpStatus.BAD_REQUEST, RideDriverManagementServiceImpl.RIDE_START_INVALID_DRIVER_LOCATION_EXCEPTION_MESSAGE)
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa50c")
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-start/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }

    @Test
    fun confirmRideStart_notValidRideStatus_shouldReturnRideStartConfirmationErrorResponse() {
        val expected = ExceptionInfo(HttpStatus.BAD_REQUEST, RideDriverManagementServiceImpl.RIDE_START_INVALID_STATUS_EXCEPTION_MESSAGE)
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa51c")
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-start/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }


    @Test
    fun confirmRideStart_validRideCondition_shouldConfirmRideStart() {
        given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa52c")
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-start/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
    }

    @Test
    fun getAvailableRides_notValidSearchRadius_shouldReturnNotValidSearchRadiusErrorResponse() {
        properties.useDefaultRadiusIfRadiusNotInRange = false
        val request = GetAvailableRidesRequest(
            id = UUID.randomUUID(),
            currentLocation = RequestPoint(BigDecimal.valueOf(40.7128),
                BigDecimal.valueOf(40.7128)),
            radius = 1500)
        val expected = ExceptionInfo(HttpStatus.BAD_REQUEST, String.format(NOT_VALID_SEARCH_RADIUS_EXCEPTION_MESSAGE, properties.minRadius, properties.maxRadius))
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .get(DRIVER_MANAGEMENT_CONTROLLER_BASE_URL)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }

    @Test
    fun getAvailableRides_notValidSearchRadius_shouldUseDefaultSearchRadius() {

        properties.useDefaultRadiusIfRadiusNotInRange = true

        val request = GetAvailableRidesRequest(
            id = UUID.randomUUID(),
            currentLocation = RequestPoint(BigDecimal.valueOf(40.1128),
                BigDecimal.valueOf(-74.003)),
            radius = 1500)
        val expected = GetAvailableRidesResponse(
            getRidesWithRadius700()
        )
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .get(DRIVER_MANAGEMENT_CONTROLLER_BASE_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .`as`(GetAvailableRidesResponse::class.java)

        assertEquals(expected, actual)

    }

    @Test
    fun getAvailableRides_validSearchRadius_shouldReturnOneRide() {

        properties.useDefaultRadiusIfRadiusNotInRange = false

        val request = GetAvailableRidesRequest(
            id = UUID.randomUUID(),
            currentLocation = RequestPoint(BigDecimal.valueOf(40.1128),
                BigDecimal.valueOf(-74.003)),
            radius = 600)
        val expected = GetAvailableRidesResponse(
            getRidesWithRadius600()
        )
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .get(DRIVER_MANAGEMENT_CONTROLLER_BASE_URL)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .`as`(GetAvailableRidesResponse::class.java)

        assertEquals(expected, actual)

    }

    @Test
    fun acceptRide_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {
        val driverId = UUID.randomUUID()
        val driverProfileModel = DriverProfileModel(
            id = driverId,
            phoneNumber = "+375295432551",
            name = "Name",
            surname = "Surname",
            rating = BigDecimal.ONE,
            experience = 3
        )
        val driverProfileModelJSON = jacksonObjectMapper().writeValueAsString(driverProfileModel)
        driverWireMock.stubFor(
            get("/api/driver/profile/$driverId")
                .willReturn(aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(driverProfileModelJSON))
        )
        val randomId = UUID.randomUUID().toString()
        val request = AcceptRideRequest(
            rideId = UUID.fromString(randomId),
            driverId = driverId,
            RequestPoint(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(5))
        )

        val expected = ExceptionInfo(HttpStatus.NOT_FOUND, String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, randomId))
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/accept")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }

    @Test
    fun acceptRide_invalidRideStatus_shouldReturnRideAlreadyAcceptedErrorResponse() {
        val driverId = UUID.randomUUID()
        val driverProfileModel = DriverProfileModel(
            id = driverId,
            phoneNumber = "+375295432551",
            name = "Name",
            surname = "Surname",
            rating = BigDecimal.ONE,
            experience = 3
        )
        val driverProfileModelJSON = jacksonObjectMapper().writeValueAsString(driverProfileModel)
        driverWireMock.stubFor(
            get("/api/driver/profile/$driverId")
                .willReturn(aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(driverProfileModelJSON))
        )
        val rideId = "4ba65be8-cd97-4d40-aeae-8eb5a71fa55c"
        val request = AcceptRideRequest(
            rideId = UUID.fromString(rideId),
            driverId = driverId,
            RequestPoint(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(5))
        )

        val expected = ExceptionInfo(HttpStatus.CONFLICT, String.format(RIDE_ALREADY_ACCEPTED_EXCEPTION_MESSAGE, rideId))
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/accept")
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }

    @Test
    fun acceptRide_correctRequest_shouldAcceptRide() {
        val driverId = UUID.randomUUID()
        val driverProfileModel = DriverProfileModel(
            id = driverId,
            phoneNumber = "+375295432551",
            name = "Name",
            surname = "Surname",
            rating = BigDecimal.ONE,
            experience = 3
        )
        val driverProfileModelJSON = jacksonObjectMapper().writeValueAsString(driverProfileModel)
        driverWireMock.stubFor(
            get("/api/driver/profile/$driverId")
                .willReturn(aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(driverProfileModelJSON))
        )
        val rideId = "4ba65be8-cd97-4d40-aeae-8eb5a71fa53c"
        val request = AcceptRideRequest(
            rideId = UUID.fromString(rideId),
            driverId = driverId,
            RequestPoint(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(5))
        )

        given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/accept")
            .then()
            .statusCode(HttpStatus.OK.value())

    }

    @Test
    fun acceptRide_driverNotExists_shouldReturnNoSuchRecordErrorResponse() {
        val driverId = UUID.randomUUID()
        val driverProfileModel = DriverProfileModel(
            id = driverId,
            phoneNumber = "+375295432551",
            name = "Name",
            surname = "Surname",
            rating = BigDecimal.ONE,
            experience = 3
        )
        val driverProfileModelJSON = jacksonObjectMapper().writeValueAsString(driverProfileModel)
        driverWireMock.stubFor(
            get("/api/driver/profile/$driverId")
                .willReturn(aResponse()
                    .withStatus(HttpStatus.NOT_FOUND.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(driverProfileModelJSON))
        )
        val randomId = UUID.randomUUID().toString()
        val request = AcceptRideRequest(
            rideId = UUID.fromString(randomId),
            driverId = driverId,
            RequestPoint(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(5))
        )

        val expected = ExceptionInfo(HttpStatus.NOT_FOUND, "Driver profile was not found")
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/accept")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }



    @Test
    fun confirmRideEnd_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {

        val randomId = UUID.randomUUID().toString()
        val expected = ExceptionInfo(HttpStatus.NOT_FOUND, String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, randomId))
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", randomId)
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-end/{id}")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }

    @Test
    fun confirmRideEnd_driverLocationIsNotValid_shouldReturnRideEndConfirmationErrorResponse() {

        val expected = ExceptionInfo(HttpStatus.BAD_REQUEST, RIDE_END_INVALID_DRIVER_LOCATION_EXCEPTION_MESSAGE)
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa56c")
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-end/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }

    @Test
    fun confirmRideEnd_notValidRideStatus_shouldReturnRideEndConfirmationErrorResponse() {
        val expected = ExceptionInfo(HttpStatus.BAD_REQUEST, RIDE_END_INVALID_STATUS_EXCEPTION_MESSAGE)
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa57c")
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-end/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }


    @Test
    fun confirmRideEnd_validRideCondition_shouldConfirmRideEnd() {
        given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa58c")
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/confirm-end/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
    }

    @Test
    fun getAllRides_validRequest_shouldReturnAllRidesForDriverWithId() {
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
        passengerWireMock.stubFor(
            get("/api/passenger/profiles/" + passengerIds.joinToString(","))
                .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(modelsJson))
        )

        driverWireMock.stubFor(
            get("/api/driver/profile/$driverId")
                .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(jacksonObjectMapper().writeValueAsString(getAllDriverRidesDriverProfileModel())))
        )

        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", driverId)
            .params(
                mapOf(
                Pair("pageNumber", 1),
                Pair("pageSize", 2),
                Pair("orderBy", "id"))
            )
            .get("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/rides/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .`as`(RidePageResponse::class.java)

        assertEquals(expected, actual)

    }

    @Test
    fun getAllRides_badParams_shouldReturnConstraintViolationErrorResponse() {

        val driverId = "d1e42c0d-aa71-448c-843f-5a5e801ed287"
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", driverId)
            .params(
                mapOf(
                    Pair("pageNumber", 0),
                    Pair("pageSize", 0),
                    Pair("orderBy", "ids"))
            )
            .get("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/rides/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, actual.status)

    }


    @Test
    fun ratePassenger_correctRequest_shouldRatePassenger() {
        val ratePassengerRequest = SendRatingRequest(
            rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa65c"),
            rating = BigDecimal.ONE
        )

        given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(ratePassengerRequest)
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/rate")
            .then()
            .statusCode(HttpStatus.OK.value())

    }

    @Test
    fun ratePassenger_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {
        val ratePassengerRequest = SendRatingRequest(
            rideId = UUID.randomUUID(),
            rating = BigDecimal.ONE
        )

        val expected = ExceptionInfo(
            HttpStatus.NOT_FOUND,
            message = String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, ratePassengerRequest.rideId)
        )
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(ratePassengerRequest)
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/rate")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)

    }

    @Test
    fun ratePassenger_rideStatusIsNotValid_shouldReturnSendRatingErrorResponse() {
        val rateDriverRequest = SendRatingRequest(
            rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa62c"),
            rating = BigDecimal.ONE
        )

        val expected = ExceptionInfo(
            HttpStatus.BAD_REQUEST,
            message = RATE_PASSENGER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE
        )
        val actual = RestAssured.given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(rateDriverRequest)
            .post("$DRIVER_MANAGEMENT_CONTROLLER_BASE_URL/rate")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)

    }

}