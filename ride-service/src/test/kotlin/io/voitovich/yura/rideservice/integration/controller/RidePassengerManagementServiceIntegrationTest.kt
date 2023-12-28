package io.voitovich.yura.rideservice.integration.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.voitovich.yura.rideservice.client.model.PassengerProfileModel
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
import io.voitovich.yura.rideservice.service.impl.RidePassengerManagementServiceImpl.Companion.NO_SUCH_RECORD_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RidePassengerManagementServiceImpl.Companion.RATE_DRIVER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RidePassengerManagementServiceImpl.Companion.RIDE_CANT_BE_CANCELED_EXCEPTION_MESSAGE
import io.voitovich.yura.rideservice.service.impl.RidePassengerManagementServiceImpl.Companion.RIDE_CANT_BE_STARTED_EXCEPTION_MESSAGE
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
        scripts = ["classpath:sql/truncate-ride-table.sql"],
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    ),
    Sql(
        scripts = ["classpath:sql/insert-test-values-in-ride-table.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = [WireMockConfig::class])
@ActiveProfiles("test")
class RidePassengerManagementServiceIntegrationTest {

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

        private const val PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL = "api/ride/passenger"

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
    fun createRide_correctRequest_shouldReturnCreateRideResponse() {
        val passengerId = UUID.randomUUID()
        val passengerProfileModel = PassengerProfileModel(
            id = passengerId,
            phoneNumber = "+375295432551",
            name = "Name",
            surname = "Surname",
            rating = BigDecimal.ONE,
        )
        val passengerProfileModelJSON = jacksonObjectMapper().writeValueAsString(passengerProfileModel)
        passengerWireMock.stubFor(
            get("/api/passenger/profile/$passengerId")
                .willReturn(aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(passengerProfileModelJSON))
        )
        val request = getDefaultCreateRideRequest(passengerId)

        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .put(PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .`as`(CreateRideResponse::class.java)

        assertEquals(passengerId, actual.passengerId)
    }

    @Test
    fun createRide_passengerProfileNotFound_shouldReturnNoSuchRecordErrorResponse() {
        val passengerId = UUID.randomUUID()

        passengerWireMock.stubFor(
            get("/api/passenger/profile/$passengerId")
                .willReturn(aResponse()
                    .withStatus(HttpStatus.NOT_FOUND.value()))
        )
        val request = getDefaultCreateRideRequest(passengerId)

        val expected = ExceptionInfo(
            status = HttpStatus.NOT_FOUND,
            message = "Passenger profile was not found"
        )
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .put(PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }

    @Test
    fun createRide_rideForPassengerExists_shouldReturnRideCantBeStartedErrorResponse() {
        val passengerId = UUID.fromString("7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10")

        val passengerProfileModel = PassengerProfileModel(
            id = passengerId,
            phoneNumber = "+375295432551",
            name = "Name",
            surname = "Surname",
            rating = BigDecimal.ONE,
        )
        val passengerProfileModelJSON = jacksonObjectMapper().writeValueAsString(passengerProfileModel)
        passengerWireMock.stubFor(
            get("/api/passenger/profile/$passengerId")
                .willReturn(aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(passengerProfileModelJSON)))

        val request = getDefaultCreateRideRequest(passengerId)

        val expected = ExceptionInfo(
            status = HttpStatus.CONFLICT,
            message = String.format(RIDE_CANT_BE_STARTED_EXCEPTION_MESSAGE, passengerId)
        )
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .put(PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL)
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }

    @Test
    fun cancelRide_rideCantBeCanceled_shouldReturnRideCantBeCanceledErrorResponse() {
        val passengerId = UUID.fromString("7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10")

        val request = CancelRequest(
            passengerId = passengerId,
            rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa63c")
        )

        val expected = ExceptionInfo(
            status = HttpStatus.CONFLICT,
            message = String.format(RIDE_CANT_BE_CANCELED_EXCEPTION_MESSAGE, request.rideId)
        )
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .delete(PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL)
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }

    @Test
    fun cancelRide_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {
        val passengerId = UUID.fromString("7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10")

        val request = CancelRequest(
            passengerId = passengerId,
            rideId = UUID.randomUUID()
        )

        val expected = ExceptionInfo(
            status = HttpStatus.NOT_FOUND,
            message = String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, request.rideId)
        )
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .delete(PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)
    }

    @Test
    fun cancelRide_correctRequest_shouldCancelRide() {
        val passengerId = UUID.fromString("7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10")

        val request = CancelRequest(
            passengerId = passengerId,
            rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa62c")
        )

        given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(request)
            .delete(PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value())


    }

    @Test
    fun getAllRides_validRequest_shouldReturnAllRidesForDriverWithId() {
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
        driverWireMock.stubFor(
            get("/api/driver/profiles/" + driverIds.joinToString(","))
                .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(modelsJson))
        )

        passengerWireMock.stubFor(
            get("/api/passenger/profile/$passengerId")
                .willReturn(aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(jacksonObjectMapper().writeValueAsString(getAllPassengerRidesPassengerProfileModel())))
        )

        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", passengerId)
            .params(
                mapOf(
                Pair("pageNumber", 1),
                Pair("pageSize", 2),
                Pair("orderBy", "id"))
            )
            .get("$PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL/rides/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .`as`(RidePageResponse::class.java)

        assertEquals(expected, actual)

    }

    @Test
    fun getAllRides_badParams_shouldReturnConstraintViolationErrorResponse() {

        val passengerId = "7e4f5342-cb2b-4e8c-8ab7-1629afcf5d10"
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .pathParam("id", passengerId)
            .params(
                mapOf(
                    Pair("pageNumber", 0),
                    Pair("pageSize", 0),
                    Pair("orderBy", "ids"))
            )
            .get("$PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL/rides/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, actual.status)

    }


    @Test
    fun rateDriver_correctRequest_shouldRateDriver() {
        val rateDriverRequest = SendRatingRequest(
            rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa65c"),
            rating = BigDecimal.ONE
        )

        given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(rateDriverRequest)
            .post("$PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL/rate")
            .then()
            .statusCode(HttpStatus.OK.value())

    }

    @Test
    fun rateDriver_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {
        val rateDriverRequest = SendRatingRequest(
            rideId = UUID.randomUUID(),
            rating = BigDecimal.ONE
        )

        val expected = ExceptionInfo(
            HttpStatus.NOT_FOUND,
            message = String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, rateDriverRequest.rideId)
        )
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(rateDriverRequest)
            .post("$PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL/rate")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)

    }

    @Test
    fun rateDriver_rideStatusIsNotValid_shouldReturnSendRatingErrorResponse() {
        val rateDriverRequest = SendRatingRequest(
            rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa62c"),
            rating = BigDecimal.ONE
        )

        val expected = ExceptionInfo(
            HttpStatus.BAD_REQUEST,
            message = RATE_DRIVER_STATUS_NOT_ALLOWED_EXCEPTION_MESSAGE
        )
        val actual = given()
            .contentType(ContentType.JSON)
            .port(port!!)
            .body(rateDriverRequest)
            .post("$PASSENGER_MANAGEMENT_CONTROLLER_BASE_URL/rate")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .`as`(ExceptionInfo::class.java)

        assertEquals(expected, actual)

    }

}