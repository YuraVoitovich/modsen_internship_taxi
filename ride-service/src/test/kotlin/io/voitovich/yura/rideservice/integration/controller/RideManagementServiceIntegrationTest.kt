package io.voitovich.yura.rideservice.integration.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.get
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.voitovich.yura.rideservice.dto.responce.RidePageResponse
import io.voitovich.yura.rideservice.dto.responce.RideResponse
import io.voitovich.yura.rideservice.exceptionhandler.model.ExceptionInfo
import io.voitovich.yura.rideservice.integration.config.WireMockConfig
import io.voitovich.yura.rideservice.integration.util.RideManagementIntegrationTestsUtils.Companion.getAllRides
import io.voitovich.yura.rideservice.integration.util.RideManagementIntegrationTestsUtils.Companion.getAllRidesDriverProfileModels
import io.voitovich.yura.rideservice.integration.util.RideManagementIntegrationTestsUtils.Companion.getAllRidesPassengerProfileModels
import io.voitovich.yura.rideservice.integration.util.Utils.Companion.executeRequest
import io.voitovich.yura.rideservice.integration.util.Utils.Companion.setupDriverWireMock
import io.voitovich.yura.rideservice.integration.util.Utils.Companion.setupDriversWireMock
import io.voitovich.yura.rideservice.integration.util.Utils.Companion.setupPassengerWireMock
import io.voitovich.yura.rideservice.integration.util.Utils.Companion.setupPassengersWireMock
import io.voitovich.yura.rideservice.properties.DefaultApplicationProperties
import io.voitovich.yura.rideservice.service.impl.RideServiceImpl.Companion.NO_SUCH_RECORD_EXCEPTION_MESSAGE
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpMethod
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
import java.util.UUID


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
class RideManagementServiceIntegrationTest {

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

        private const val RIDE_MANAGEMENT_CONTROLLER_BASE_URL = "api/ride"

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
    fun getRideById_rideNotExists_shouldReturnNoSuchRecordErrorResponse() {
        // Arrange
        val rideId = UUID.randomUUID()
        val expected = ExceptionInfo(
            status = HttpStatus.NOT_FOUND,
            message = String.format(NO_SUCH_RECORD_EXCEPTION_MESSAGE, rideId)
        )

        // Act
        val actual = executeRequest(
            port = port!!,
            url = "$RIDE_MANAGEMENT_CONTROLLER_BASE_URL/$rideId",
            method = HttpMethod.GET,
            expectedStatus = HttpStatus.NOT_FOUND,
            extractClass = ExceptionInfo::class.java
        )

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun getRideById_rideExists_shouldReturnRideResponse() {
        // Arrange
        val rideId = UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa50c")
        val expected = getAllRides()[0]

        val passengerModel = getAllRidesPassengerProfileModels().models[0]
        val driverModel = getAllRidesDriverProfileModels().models[0]

        val passengerModelJson = jacksonObjectMapper().writeValueAsString(passengerModel)
        val driverModelJson = jacksonObjectMapper().writeValueAsString(driverModel)
        setupPassengerWireMock(
            passengerWireMock,
            passengerModel.id.toString(),
            HttpStatus.OK,
            passengerModelJson
        )

        setupDriverWireMock(
            driverWireMock,
            driverModel.id.toString(),
            HttpStatus.OK,
            driverModelJson
        )

        // Act
        val actual = executeRequest(
            port = port!!,
            url = "$RIDE_MANAGEMENT_CONTROLLER_BASE_URL/$rideId",
            method = HttpMethod.GET,
            expectedStatus = HttpStatus.OK,
            extractClass = RideResponse::class.java
        )

        // Assert
        assertEquals(expected, actual)
    }


    @Test
    fun getRidePage_correctRequest_shouldReturnRidePageResponse() {
        // Arrange
        val expected = RidePageResponse(
            profiles = getAllRides(),
            pageNumber = 1,
            totalElements = 16,
            totalPages = 8
        )

        val passengerModels = getAllRidesPassengerProfileModels()
        val driverModels = getAllRidesDriverProfileModels()

        val passengerIds = passengerModels.models.map { it.id.toString() }.toList()
        val driverIds = driverModels.models.map { it.id.toString() }.toList()

        val passengerModelsJson = jacksonObjectMapper().writeValueAsString(passengerModels)
        val driverModelsJson = jacksonObjectMapper().writeValueAsString(driverModels)

        // Act
        setupPassengersWireMock(
            passengerWireMock,
            passengerIds,
            HttpStatus.OK,
            passengerModelsJson
        )

        setupDriversWireMock(
            driverWireMock,
            driverIds,
            HttpStatus.OK,
            driverModelsJson
        )

        val actual = executeRequest(
            port = port!!,
            url = RIDE_MANAGEMENT_CONTROLLER_BASE_URL,
            method = HttpMethod.GET,
            expectedStatus = HttpStatus.OK,
            extractClass = RidePageResponse::class.java,
            params = mapOf(
                Pair("pageNumber", 1),
                Pair("pageSize", 2),
                Pair("orderBy", "id")
            )
        )

        // Assert
        assertEquals(expected, actual)
    }


    @Test
    fun getRidePage_badParams_shouldReturnConstraintViolationErrorResponse() {
        // Act
        val actual = executeRequest(
            port = port!!,
            url = RIDE_MANAGEMENT_CONTROLLER_BASE_URL,
            method = HttpMethod.GET,
            expectedStatus = HttpStatus.BAD_REQUEST,
            extractClass = ExceptionInfo::class.java,
            params = mapOf(
                Pair("pageNumber", 0),
                Pair("pageSize", 0),
                Pair("orderBy", "ids")
            )
        )

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, actual.status)
    }

}