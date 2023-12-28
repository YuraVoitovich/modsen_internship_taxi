package io.voitovich.yura.driverservice.integration.controller;


import io.restassured.http.ContentType;
import io.voitovich.yura.driverservice.dto.request.DriverProfileSaveRequest;
import io.voitovich.yura.driverservice.dto.request.DriverProfileUpdateRequest;
import io.voitovich.yura.driverservice.dto.response.DriverProfilePageResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfileResponse;
import io.voitovich.yura.driverservice.dto.response.DriverProfilesResponse;
import io.voitovich.yura.driverservice.exceptionhandler.model.ExceptionInfo;
import io.voitovich.yura.driverservice.exceptionhandler.model.ValidationExceptionInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@SqlGroup({
        @Sql(scripts = {
                "classpath:sql/truncate-driver-profile-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
        @Sql(scripts = {
                "classpath:sql/insert-test-values-in-driver-profile-table.sql"
        },
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        }
)
@ActiveProfiles("test")
public class DriverServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @LocalServerPort
    private Integer port;

    private final String DRIVER_SERVICE_BASE_URL = "api/driver/profile";

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        propertyRegistry.add("spring.datasource.url", postgres::getJdbcUrl);
        propertyRegistry.add("spring.datasource.username", postgres::getUsername);
        propertyRegistry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void getProfileById_profileExists_shouldReturnPassengerProfileResponse() {

        var expected = DriverProfileResponse.builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("John")
                .experience(3)
                .surname("Doe")
                .rating(BigDecimal.valueOf(5.0))
                .phoneNumber("+375295432551")
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa58c")
                .when()
                .get(DRIVER_SERVICE_BASE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverProfileResponse.class);

        assertEquals(expected, actual);

    }

    @Test
    void getProfileById_profileNotExists_shouldReturnNoSuchRecordErrorResponse() {

        var expected = ExceptionInfo.builder()
                .message("")
                .status(HttpStatus.NOT_FOUND)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .pathParam("id", UUID.randomUUID().toString())
                .when()
                .get(DRIVER_SERVICE_BASE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());

    }


    @Test
    void updateProfile_profileCanBeUpdated_shouldReturnUpdatedPassengerProfileResponse() {

        var updateRequest = DriverProfileUpdateRequest
                .builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("Joe")
                .experience(7)
                .surname("Fanski")
                .phoneNumber("+375295432555")
                .build();
        var expected = DriverProfileResponse.builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("Joe")
                .experience(7)
                .surname("Fanski")
                .rating(BigDecimal.valueOf(5.0))
                .phoneNumber("+375295432555")
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(updateRequest)
                .when()
                .post(DRIVER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverProfileResponse.class);

        assertEquals(expected, actual);

    }


    @Test
    void updateProfile_profileNotExists_shouldReturnNoSuchRecordErrorResponse() {

        var updateRequest = DriverProfileUpdateRequest
                .builder()
                .id(UUID.randomUUID())
                .name("Joe")
                .experience(4)
                .surname("Fanski")
                .phoneNumber("+375295432555")
                .build();
        var expected = ExceptionInfo.builder()
                .message("")
                .status(HttpStatus.NOT_FOUND)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(updateRequest)
                .when()
                .post(DRIVER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());

    }

    @Test
    void updateProfile_profileWithThisPhoneNumberExists_shouldReturnNotUniquePhoneErrorResponse() {

        var updateRequest = DriverProfileUpdateRequest
                .builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("Joe")
                .experience(6)
                .surname("Fanski")
                .phoneNumber("+375295432552")
                .build();
        var expected = ExceptionInfo.builder()
                .message("")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(updateRequest)
                .when()
                .post(DRIVER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());

    }

    @Test
    void updateProfile_badPassengerProfileUpdateRequest_shouldReturnValidationErrorResponse() {

        var updateRequest = DriverProfileUpdateRequest
                .builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("")
                .surname("")
                .experience(-1)
                .phoneNumber("+214")
                .build();
        var expected = ValidationExceptionInfo.builder()
                .error("name", "")
                .error("phoneNumber", "")
                .error("surname", "")
                .error("experience", "")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(updateRequest)
                .when()
                .post(DRIVER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationExceptionInfo.class);

        expected.errors().keySet()
                .forEach((key) -> assertTrue(actual.errors().containsKey(key)));
        assertEquals(expected.status(), actual.status());
    }


    @Test
    void saveProfile_badPassengerProfileCreateRequest_shouldReturnValidationErrorResponse() {

        var saveRequest = DriverProfileSaveRequest
                .builder()
                .name("")
                .experience(-1)
                .surname("")
                .phoneNumber("+214")
                .build();
        var expected = ValidationExceptionInfo.builder()
                .error("name", "")
                .error("phoneNumber", "")
                .error("surname", "")
                .error("experience", "")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(saveRequest)
                .when()
                .put(DRIVER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ValidationExceptionInfo.class);

        expected.errors().keySet()
                .forEach((key) -> assertTrue(actual.errors().containsKey(key)));
        assertEquals(expected.status(), actual.status());
    }

    @Test
    void saveProfile_profileWithThisPhoneNumberExists_shouldReturnNotUniquePhoneErrorResponse() {

        var saveRequest = DriverProfileSaveRequest
                .builder()
                .name("Joe")
                .experience(4)
                .surname("Fanski")
                .phoneNumber("+375295432552")
                .build();
        var expected = ExceptionInfo.builder()
                .message("")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(saveRequest)
                .when()
                .put(DRIVER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());

    }

    @Test
    void saveProfile_profileCanBeSaved_shouldReturnSavedPassengerProfileResponse() {

        var saveRequest = DriverProfileSaveRequest
                .builder()
                .name("Joe")
                .surname("Fanski")
                .experience(3)
                .phoneNumber("+375295432555")
                .build();
        var expected = DriverProfileResponse.builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("Joe")
                .experience(3)
                .surname("Fanski")
                .rating(BigDecimal.valueOf(5))
                .phoneNumber("+375295432555")
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .body(saveRequest)
                .when()
                .put(DRIVER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(DriverProfileResponse.class);

        assertEquals(expected.surname(), actual.surname());
        assertEquals(expected.rating(), actual.rating());
        assertEquals(expected.phoneNumber(), actual.phoneNumber());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.experience(), actual.experience());

    }

    @Test
    void deleteProfileById_profileExists_shouldDeleteProfile() {

        given()
                .contentType(ContentType.JSON)
                .port(port)
                .pathParam("id", "4ba65be8-cd97-4d40-aeae-8eb5a71fa58c")
                .when()
                .delete(DRIVER_SERVICE_BASE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

    }

    @Test
    void deleteProfileById_profileNotExists_shouldReturnNoSuchRecordErrorResponse() {

        var expected = ExceptionInfo.builder()
                .message("")
                .status(HttpStatus.NOT_FOUND)
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .pathParam("id", UUID.randomUUID().toString())
                .when()
                .delete(DRIVER_SERVICE_BASE_URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());

    }


    @Test
    void getProfilePage_badParams_shouldReturnConstraintViolationErrorResponse() {

        var expected = ExceptionInfo.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("")
                .build();
        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .params(Map.of(
                        "pageNumber", 0,
                        "pageSize", 0,
                        "orderBy", "ids"))
                .when()
                .get(DRIVER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .as(ExceptionInfo.class);

        assertEquals(expected.status(), actual.status());
    }

    @Test
    void getProfilePage_correctParams_shouldReturnPageResponse() {

        var expected = DriverProfilePageResponse
                .builder()
                .profiles(getDefaultSortedDriverProfilesResponseList())
                .pageNumber(1)
                .totalPages(2)
                .totalElements(3)
                .build();

        var actual = given()
                .contentType(ContentType.JSON)
                .port(port)
                .params(Map.of(
                        "pageNumber", 1,
                        "pageSize", 2,
                        "orderBy", "phoneNumber"))
                .when()
                .get(DRIVER_SERVICE_BASE_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverProfilePageResponse.class);

        assertEquals(expected, actual);
    }

    @Test
    void getByIds_correctIds_shouldReturnDriverProfiles() {
        var profiles = getDefaultSortedDriverProfilesResponseList();
        var ids = profiles.stream().map(DriverProfileResponse::id).toList();
        var expected = DriverProfilesResponse.builder()
                .profiles(profiles)
                .build();
        var actual = given().contentType(ContentType.JSON)
                .port(port)
                .pathParam("ids", String.join(",", ids.stream().map(UUID::toString).toList()))
                .when()
                .get("api/driver/profiles/{ids}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(DriverProfilesResponse.class);

        assertEquals(expected, actual);
    }

    private List<DriverProfileResponse> getDefaultSortedDriverProfilesResponseList() {
        var first = DriverProfileResponse.builder()
                .id(UUID.fromString("4ba65be8-cd97-4d40-aeae-8eb5a71fa58c"))
                .name("John")
                .experience(3)
                .surname("Doe")
                .rating(BigDecimal.valueOf(5.0))
                .phoneNumber("+375295432551")
                .build();
        var second = DriverProfileResponse.builder()
                .id(UUID.fromString("025fe6d1-8363-4a1a-925d-d91a8b640b8f"))
                .name("Jane")
                .surname("Smith")
                .experience(4)
                .rating(BigDecimal.valueOf(5.0))
                .phoneNumber("+375295432552")
                .build();
        return List.of(first, second);
    }



}
